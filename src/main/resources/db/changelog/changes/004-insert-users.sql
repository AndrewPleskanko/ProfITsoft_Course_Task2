INSERT INTO app_user (username, password, email, role_id)
VALUES ('john', '$2a$10$5vvbROzmmXGkfPVaZTyNjODxOEBkobazyMcGWaoSKugSaMLSER0Pq', 'john@example.com',
        (SELECT id FROM role WHERE name = 'ROLE_USER'));

INSERT INTO app_user (username, password, email, role_id)
VALUES ('jane', '$2a$10$5vvbROzmmXGkfPVaZTyNjODxOEBkobazyMcGWaoSKugSaMLSER0Pq', 'jane@example.com',
        (SELECT id FROM role WHERE name = 'ROLE_USER'));

INSERT INTO app_user (username, password, email, role_id)
VALUES ('admin', '$2a$10$H7Tw60M/fe.vwwBgxCTvYuDrHGOhJ6s.yxArIrjsgfQOwL6lR2RdO', 'admin@example.com',
        (SELECT id FROM role WHERE name = 'ROLE_ADMIN'));

INSERT INTO app_user (username, password, email, role_id)
VALUES ('moder', '$2a$10$TWJR3OY0oCMUqxRbnJVw1eREnjYUIyI24IbFgox.Fpf6PD0ZJa2Au', 'moder@example.com',
        (SELECT id FROM role WHERE name = 'ROLE_MODERATOR'));