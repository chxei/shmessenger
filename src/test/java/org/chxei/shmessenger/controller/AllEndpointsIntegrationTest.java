package org.chxei.shmessenger.controller;

import com.tinify.Source;
import com.tinify.Tinify;
import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"server.ssl.enabled=false", "spring.main.lazy-initialization=false"})
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.servlet.multipart.enabled=true")
class AllEndpointsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void authEndpointsRegisterLoginAndChangePassword() throws Exception {
        String username = unique("auth");
        String password = "oldPassword";

        register(username, password)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseType").value("OK"));

        mockMvc.perform(post("/auth/login")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwt", notNullValue()))
                .andExpect(jsonPath("$.jwt", matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$")))
                .andExpect(jsonPath("$.userId", greaterThanOrEqualTo(1)));

        mockMvc.perform(put("/auth/password")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "oldPassword",
                                  "newPassword": "newPassword"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseType").value("OK"));

        mockMvc.perform(post("/auth/login")
                        .with(httpBasic(username, "newPassword"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt", notNullValue()));
    }

    @Test
    void lookupAndUserEndpointsReturnDatabaseBackedResponses() throws Exception {
        String username = unique("users");
        register(username, "password").andExpect(status().isOk());
        User user = userRepository.findByUsername(username).orElseThrow();

        mockMvc.perform(get("/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].name").exists());

        mockMvc.perform(get("/genders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists());

        mockMvc.perform(get("/users").with(httpBasic(username, "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").exists());

        mockMvc.perform(get("/users/{id}", user.getId()).with(httpBasic(username, "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(username));

        mockMvc.perform(get("/users/{id}", 999_999).with(httpBasic(username, "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("USER_WITH_ID_NOT_FOUND"));

        mockMvc.perform(get("/users/username/{username}", username).with(httpBasic(username, "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(username));

        mockMvc.perform(get("/users/username/{username}", unique("missing")).with(httpBasic(username, "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("USER_WITH_USERNAME_NOT_FOUND"));

        mockMvc.perform(get("/users/testAuth").with(httpBasic(username, "password")))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, " + username + "!"));
    }

    @Test
    void chatEndpointsCreateConversationAndSendMessage() throws Exception {
        String creator = unique("creator");
        String participant = unique("participant");
        register(creator, "password").andExpect(status().isOk());
        register(participant, "password").andExpect(status().isOk());
        int participantId = userRepository.findByUsername(participant).orElseThrow().getId();

        String conversationResponse = mockMvc.perform(post("/chats/conversations")
                        .with(httpBasic(creator, "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "conversationName": "Integration chat",
                                  "participantIds": [%d]
                                }
                                """.formatted(participantId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId", greaterThanOrEqualTo(1)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long conversationId = Long.parseLong(conversationResponse.replaceAll("\\D+", ""));

        mockMvc.perform(post("/chats/messages")
                        .with(httpBasic(creator, "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "conversationId": %d,
                                  "messageTypeName": "COMPOSED",
                                  "content": "hello"
                                }
                                """.formatted(conversationId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId", greaterThanOrEqualTo(1)));
    }

    @Test
    void fileEndpointsUploadAndDownloadImage() throws Exception {
        String username = unique("files");
        register(username, "password").andExpect(status().isOk());

        Source source = mock(Source.class);
        when(source.toBuffer()).thenReturn("compressed-image".getBytes());

        String uploadResponse;
        try (MockedStatic<Tinify> tinify = mockStatic(Tinify.class)) {
            tinify.when(() -> Tinify.fromBuffer("original-image".getBytes())).thenReturn(source);

            MockMultipartFile multipartFile = new MockMultipartFile(
                    "multipartFile",
                    "../photo.jpg",
                    "image/jpeg",
                    "original-image".getBytes());

            uploadResponse = mockMvc.perform(multipart("/files")
                            .file(multipartFile)
                            .with(httpBasic(username, "password")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(matchesPattern("\\d+")))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }

        mockMvc.perform(get("/files/{fileId}", Long.parseLong(uploadResponse)).with(httpBasic(username, "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes("compressed-image".getBytes()));
    }

    private org.springframework.test.web.servlet.ResultActions register(String username, String password) throws Exception {
        return mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "%s",
                          "name": "%s",
                          "email": "%s@example.com",
                          "phone": "%s",
                          "password": "%s",
                          "countryCode": "GE",
                          "genderId": 1
                        }
                        """.formatted(username, username, username, username, password)));
    }

    private String unique(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
    }
}
