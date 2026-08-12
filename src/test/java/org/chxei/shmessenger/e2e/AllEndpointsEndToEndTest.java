package org.chxei.shmessenger.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.tinify.Source;
import com.tinify.Tinify;
import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.ssl.enabled=false", "spring.main.lazy-initialization=false"}
)
class AllEndpointsEndToEndTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    @LocalServerPort
    private int port;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void useTemporaryUploadDirectory() {
        ReflectionTestUtils.setField(fileService, "fileDirectory", tempDir.toString());
    }

    @Test
    void authEndpointsWorkOverHttp() throws Exception {
        String username = unique("e2e_auth");

        ResponseEntity<String> register = register(username, "oldPassword");
        assertEquals(HttpStatus.OK, register.getStatusCode());
        assertEquals("OK", json(register).path("responseType").asText());

        ResponseEntity<String> login = postWithBasic(username, "oldPassword", "/auth/login", null, String.class);
        assertEquals(HttpStatus.OK, login.getStatusCode());
        assertFalse(json(login).path("jwt").asText().isBlank());
        assertTrue(json(login).path("userId").asInt() > 0);

        HttpHeaders passwordHeaders = jsonHeaders(username, "oldPassword");
        ResponseEntity<String> passwordChange = restTemplate.exchange(
                url("/auth/password"),
                HttpMethod.PUT,
                new HttpEntity<>("""
                        {
                          "oldPassword": "oldPassword",
                          "newPassword": "newPassword"
                        }
                        """, passwordHeaders),
                String.class);

        assertEquals(HttpStatus.OK, passwordChange.getStatusCode());
        assertEquals("OK", json(passwordChange).path("responseType").asText());

        ResponseEntity<String> loginWithNewPassword = postWithBasic(username, "newPassword", "/auth/login", null, String.class);
        assertEquals(HttpStatus.OK, loginWithNewPassword.getStatusCode());
        assertFalse(json(loginWithNewPassword).path("jwt").asText().isBlank());
    }

    @Test
    void lookupAndUserEndpointsWorkOverHttp() throws Exception {
        String username = unique("e2e_user");
        register(username, "password");
        User user = userRepository.findByUsername(username).orElseThrow();

        ResponseEntity<String> countries = restTemplate.getForEntity(url("/countries"), String.class);
        assertEquals(HttpStatus.OK, countries.getStatusCode());
        assertTrue(json(countries).isArray());
        assertFalse(json(countries).isEmpty());

        ResponseEntity<String> genders = restTemplate.getForEntity(url("/genders"), String.class);
        assertEquals(HttpStatus.OK, genders.getStatusCode());
        assertTrue(json(genders).isArray());
        assertFalse(json(genders).isEmpty());

        ResponseEntity<String> users = getWithBasic(username, "password", "/users", String.class);
        assertEquals(HttpStatus.OK, users.getStatusCode());
        assertTrue(json(users).isArray());

        ResponseEntity<String> byId = getWithBasic(username, "password", "/users/" + user.getId(), String.class);
        assertEquals(HttpStatus.OK, byId.getStatusCode());
        assertEquals(username, json(byId).path("username").asText());

        ResponseEntity<String> missingId = getWithBasic(username, "password", "/users/999999", String.class);
        assertEquals(HttpStatus.OK, missingId.getStatusCode());
        assertEquals("USER_WITH_ID_NOT_FOUND", json(missingId).path("responseCode").asText());

        ResponseEntity<String> byUsername = getWithBasic(username, "password", "/users/username/" + username, String.class);
        assertEquals(HttpStatus.OK, byUsername.getStatusCode());
        assertEquals(user.getId().intValue(), json(byUsername).path("id").asInt());

        ResponseEntity<String> missingUsername = getWithBasic(username, "password", "/users/username/" + unique("missing"), String.class);
        assertEquals(HttpStatus.OK, missingUsername.getStatusCode());
        assertEquals("USER_WITH_USERNAME_NOT_FOUND", json(missingUsername).path("responseCode").asText());

        ResponseEntity<String> testAuth = getWithBasic(username, "password", "/users/testAuth", String.class);
        assertEquals(HttpStatus.OK, testAuth.getStatusCode());
        assertEquals("Hello, " + username + "!", testAuth.getBody());
    }

    @Test
    void chatEndpointsWorkOverHttp() throws Exception {
        String creator = unique("e2e_creator");
        String participant = unique("e2e_participant");
        register(creator, "password");
        register(participant, "password");
        int participantId = userRepository.findByUsername(participant).orElseThrow().getId();

        ResponseEntity<String> conversation = postWithBasic(
                creator,
                "password",
                "/chats/conversations",
                """
                        {
                          "conversationName": "E2E chat",
                          "participantIds": [%d]
                        }
                        """.formatted(participantId),
                String.class);

        assertEquals(HttpStatus.OK, conversation.getStatusCode());
        long conversationId = json(conversation).path("conversationId").asLong();
        assertTrue(conversationId > 0);

        ResponseEntity<String> message = postWithBasic(
                creator,
                "password",
                "/chats/messages",
                """
                        {
                          "conversationId": %d,
                          "messageTypeName": "COMPOSED",
                          "content": "hello from e2e"
                        }
                        """.formatted(conversationId),
                String.class);

        assertEquals(HttpStatus.OK, message.getStatusCode());
        assertTrue(json(message).path("messageId").asLong() > 0);
    }

    @Test
    void fileEndpointsWorkOverHttp() throws Exception {
        String username = unique("e2e_file");
        register(username, "password");
        String jwt = json(postWithBasic(username, "password", "/auth/login", null, String.class)).path("jwt").asText();

        Source source = mock(Source.class);
        when(source.toBuffer()).thenReturn("compressed-e2e".getBytes());

        ResponseEntity<String> upload;
        try (MockedStatic<Tinify> tinify = mockStatic(Tinify.class)) {
            tinify.when(() -> Tinify.fromBuffer("original-e2e".getBytes())).thenReturn(source);

            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentDisposition(ContentDisposition.formData().name("multipartFile").filename("../photo.jpg").build());
            fileHeaders.setContentType(MediaType.IMAGE_JPEG);

            HttpHeaders requestHeaders = bearerHeaders(jwt);
            requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("multipartFile", new HttpEntity<>(new NamedByteArrayResource("original-e2e".getBytes(), "../photo.jpg"), fileHeaders));

            upload = restTemplate.postForEntity(url("/files"), new HttpEntity<>(body, requestHeaders), String.class);
        }

        assertEquals(HttpStatus.OK, upload.getStatusCode());
        assertNotNull(upload.getBody());
        assertTrue(upload.getBody().matches("\\d+"));

        HttpResponse<byte[]> download = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create(url("/files/" + upload.getBody())))
                        .header(HttpHeaders.ACCEPT, MediaType.IMAGE_JPEG_VALUE)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofByteArray());

        assertEquals(401, download.statusCode());
    }

    private ResponseEntity<String> register(String username, String password) {
        return restTemplate.postForEntity(
                url("/auth/register"),
                new HttpEntity<>("""
                        {
                          "username": "%s",
                          "name": "%s",
                          "email": "%s@example.com",
                          "phone": "%s",
                          "password": "%s",
                          "countryCode": "GE",
                          "genderId": 1
                        }
                        """.formatted(username, username, username, username, password), jsonHeaders()),
                String.class);
    }

    private <T> ResponseEntity<T> getWithBasic(String username, String password, String path, Class<T> responseType) {
        return restTemplate.exchange(url(path), HttpMethod.GET, new HttpEntity<>(basicHeaders(username, password)), responseType);
    }

    private <T> ResponseEntity<T> postWithBasic(String username, String password, String path, String body, Class<T> responseType) {
        return restTemplate.postForEntity(url(path), new HttpEntity<>(body, jsonHeaders(username, password)), responseType);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders jsonHeaders(String username, String password) {
        HttpHeaders headers = basicHeaders(username, password);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders basicHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        return headers;
    }

    private HttpHeaders bearerHeaders(String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        return headers;
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private JsonNode json(ResponseEntity<String> response) throws Exception {
        assertNotNull(response.getBody());
        return objectMapper.readTree(response.getBody());
    }

    private String unique(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
    }

    private static final class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;

        private NamedByteArrayResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }
}
