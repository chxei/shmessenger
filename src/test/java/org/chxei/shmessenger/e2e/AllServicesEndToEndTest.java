package org.chxei.shmessenger.e2e;

import com.tinify.Source;
import com.tinify.Tinify;
import org.chxei.shmessenger.entity.File;
import org.chxei.shmessenger.entity.chat.Conversation;
import org.chxei.shmessenger.entity.chat.Message;
import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.FileRepository;
import org.chxei.shmessenger.repository.chat.ConversationRepository;
import org.chxei.shmessenger.repository.chat.MessageRepository;
import org.chxei.shmessenger.repository.chat.ParticipantRepository;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.service.ChatService;
import org.chxei.shmessenger.service.FileService;
import org.chxei.shmessenger.service.UserService;
import org.chxei.shmessenger.utils.response.CustomResponseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {"server.ssl.enabled=false", "spring.main.lazy-initialization=false"})
class AllServicesEndToEndTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @TempDir
    private Path tempDir;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void userServiceEndToEndWorkflow() {
        String username = unique("e2e_service_user");
        User user = user(username, "rawPassword");

        var registerResponse = userService.registerUser(user);
        assertEquals("OK", registerResponse.getResponseType().name());
        assertTrue(userService.userExists(username));

        User loaded = userService.loadUserByUsername(username);
        assertTrue(passwordEncoder.matches("rawPassword", loaded.getPassword()));

        userService.updatePassword(loaded, "updatedPassword");
        assertTrue(passwordEncoder.matches("updatedPassword", userRepository.findByUsername(username).orElseThrow().getPassword()));

        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(username, "ignored"));
        userService.changePassword("updatedPassword", "changedPassword");
        assertTrue(passwordEncoder.matches("changedPassword", userRepository.findByUsername(username).orElseThrow().getPassword()));

        String managedUsername = unique("e2e_managed_user");
        User managed = user(managedUsername, "password");
        userService.createUser(managed);
        assertTrue(userService.userExists(managedUsername));

        managed.setName("Updated Managed User");
        userService.updateUser(managed);
        assertEquals("Updated Managed User", userRepository.findByUsername(managedUsername).orElseThrow().getName());

        userService.deleteUser(managedUsername);
        assertFalse(userService.userExists(managedUsername));
    }

    @Test
    void chatServiceEndToEndWorkflow() throws CustomResponseException {
        User creator = saveUser(unique("e2e_creator"));
        User participant = saveUser(unique("e2e_participant"));
        User outsider = saveUser(unique("e2e_outsider"));

        assertEquals(creator.getName() + ", " + participant.getName(),
                chatService.generateConversationName(List.of(creator, participant)));

        long conversationId = chatService.createConversation(creator.getUsername(), null, List.of(participant.getId()));
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow();

        assertTrue(conversation.getName().contains(creator.getName()));
        assertTrue(participantRepository.findByConversationAndUser(conversation, creator).isPresent());
        assertTrue(participantRepository.findByConversationAndUser(conversation, participant).isPresent());

        long messageId = chatService.sendMessage(creator.getUsername(), "COMPOSED", conversationId, "hello");
        Message message = messageRepository.findById(messageId).orElseThrow();

        assertEquals(creator, message.getUser());
        assertEquals(conversation, message.getConversation());
        assertEquals("COMPOSED", message.getMessageType().getName());
        assertEquals("hello", message.getContent());

        assertThrows(CustomResponseException.class,
                () -> chatService.sendMessage(outsider.getUsername(), "COMPOSED", conversationId, "blocked"));
        assertThrows(CustomResponseException.class,
                () -> chatService.createConversation(creator.getUsername(), null, List.of(999_999)));
    }

    @Test
    void fileServiceEndToEndWorkflow() throws Exception {
        ReflectionTestUtils.setField(fileService, "fileDirectory", tempDir.toString());

        Source source = mock(Source.class);
        when(source.toBuffer()).thenReturn("compressed-service-e2e".getBytes());

        Long fileId;
        try (MockedStatic<Tinify> tinify = mockStatic(Tinify.class)) {
            tinify.when(() -> Tinify.fromBuffer("original-service-e2e".getBytes())).thenReturn(source);

            fileId = fileService.saveFile(new MockMultipartFile(
                    "multipartFile",
                    "../photo.jpg",
                    "image/jpeg",
                    "original-service-e2e".getBytes()));
        }

        File savedFile = fileRepository.findById(fileId).orElseThrow();
        assertEquals("photo.jpg", savedFile.getName());
        assertArrayEquals("original-service-e2e".getBytes(), savedFile.getFileObject());
        assertArrayEquals("compressed-service-e2e".getBytes(), savedFile.getFileCompressed());
        assertTrue(Files.exists(tempDir.resolve("photo.jpg")));

        Resource downloaded = fileService.downloadImage(fileId);
        assertArrayEquals("compressed-service-e2e".getBytes(), downloaded.getContentAsByteArray());
    }

    private User user(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setName(username);
        user.setEmail(username + "@example.com");
        user.setPhone(username);
        user.setPassword(password);
        return user;
    }

    private User saveUser(String username) {
        User user = user(username, passwordEncoder.encode("password"));
        return userRepository.save(user);
    }

    private String unique(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
    }
}
