package com.smartdoc.ai.repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentRepository {

    private final JdbcTemplate jdbc;

    public DocumentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(String id, String filename, int pages, int chunks) {
        jdbc.update("INSERT INTO documents (id, filename, page_count, chunk_count) VALUES (?, ?, ?, ?)",
                id, filename, pages, chunks);
    }

    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList(
                "SELECT id AS \"documentId\", filename, page_count AS \"pageCount\", "
                + "chunk_count AS \"chunkCount\", uploaded_at AS \"uploadedAt\" "
                + "FROM documents ORDER BY uploaded_at DESC");
    }

    public int delete(String id) {
        return jdbc.update("DELETE FROM documents WHERE id = ?", id);
    }
}