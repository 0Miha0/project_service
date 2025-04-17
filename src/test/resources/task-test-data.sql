INSERT INTO project (name, description, status, visibility)
VALUES ('Project A', 'A test project', 'ACTIVE', 'PUBLIC');

INSERT INTO team (project_id)
VALUES (1);

INSERT INTO team_member (user_id, team_id)
VALUES (1, 1),
       (2, 1);

INSERT INTO team_member_roles (team_member_id, role)
VALUES (1, 'DEVELOPER'),
       (2, 'MANAGER');

INSERT INTO task (name, description, status, performer_user_id, reporter_user_id, project_id)
VALUES ('Task 1', 'A test task', 'IN_PROGRESS', 1, 2, 1),
       ('Task 2', 'Description for Task 2', 'DONE', 1, 2, 1);

INSERT INTO project_stage (project_stage_name, project_id)
VALUES ('Planning', 1);

INSERT INTO project_stage_executors (stage_id, executor_id)
VALUES (1, 1);