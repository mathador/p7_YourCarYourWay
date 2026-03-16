package com.ycyw.userservice.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycyw.userservice.model.User;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private static final Path STORAGE_FILE = Path.of("user-service-data.json");

    private final ObjectMapper objectMapper;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public UserRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        ensureStorageExists();
    }

    private void ensureStorageExists() {
        lock.writeLock().lock();
        try {
            if (Files.notExists(STORAGE_FILE)) {
                Files.writeString(STORAGE_FILE, "[]", StandardOpenOption.CREATE_NEW);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot initialize user storage", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private List<User> readAll() {
        lock.readLock().lock();
        try {
            var content = Files.readString(STORAGE_FILE);
            if (content.isBlank()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(content, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read user storage", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void persistAll(List<User> users) {
        lock.writeLock().lock();
        try {
            var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(users);
            Files.writeString(STORAGE_FILE, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write user storage", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<User> findAll() {
        return Collections.unmodifiableList(readAll());
    }

    public Optional<User> findById(String id) {
        return readAll().stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public Optional<User> findByUsername(String username) {
        return readAll().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    public User save(User user) {
        var users = new ArrayList<>(readAll());
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
        persistAll(users);
        return user;
    }

    public boolean deleteById(String id) {
        var users = new ArrayList<>(readAll());
        var removed = users.removeIf(u -> u.getId().equals(id));
        if (removed) {
            persistAll(users);
        }
        return removed;
    }
}
