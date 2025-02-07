CREATE TABLE country (
                         id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                         title varchar(64) UNIQUE NOT NULL
);

CREATE TABLE users (
                       id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                       username varchar(64) UNIQUE NOT NULL,
                       password varchar(128) NOT NULL,
                       email varchar(64) UNIQUE NOT NULL,
                       phone varchar(32) UNIQUE,
                       about_me varchar(4096),
                       active boolean DEFAULT true NOT NULL,
                       city varchar(64),
                       country_id bigint NOT NULL,
                       experience int,
                       created_at timestamptz DEFAULT current_timestamp,
                       updated_at timestamptz DEFAULT current_timestamp,

                       CONSTRAINT fk_country_id FOREIGN KEY (country_id) REFERENCES country (id)
);

CREATE TABLE subscription (
                              id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                              follower_id bigint NOT NULL,
                              followee_id bigint NOT NULL,
                              created_at timestamptz DEFAULT current_timestamp,
                              updated_at timestamptz DEFAULT current_timestamp,

                              CONSTRAINT fk_follower_id FOREIGN KEY (follower_id) REFERENCES users (id),
                              CONSTRAINT fk_followee_id FOREIGN KEY (followee_id) REFERENCES users (id)
);

CREATE TABLE mentorship (
                            id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                            mentor_id bigint NOT NULL,
                            mentee_id bigint NOT NULL,
                            created_at timestamptz DEFAULT current_timestamp,
                            updated_at timestamptz DEFAULT current_timestamp,

                            CONSTRAINT fk_mentor_id FOREIGN KEY (mentor_id) REFERENCES users (id),
                            CONSTRAINT fk_mentee_id FOREIGN KEY (mentee_id) REFERENCES users (id)
);

CREATE TABLE mentorship_request (
                                    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                    description varchar(4096) NOT NULL,
                                    requester_id bigint NOT NULL,
                                    receiver_id bigint NOT NULL,
                                    status smallint DEFAULT 0 NOT NULL,
                                    rejection_reason varchar(4096),
                                    created_at timestamptz DEFAULT current_timestamp,
                                    updated_at timestamptz DEFAULT current_timestamp,

                                    CONSTRAINT fk_mentee_req_id FOREIGN KEY (requester_id) REFERENCES users (id),
                                    CONSTRAINT fk_mentor_req_id FOREIGN KEY (receiver_id) REFERENCES users (id)
);

CREATE TABLE skill (
                       id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                       title varchar(64) UNIQUE NOT NULL,
                       created_at timestamptz DEFAULT current_timestamp,
                       updated_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE user_skill (
                            id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                            user_id bigint NOT NULL,
                            skill_id bigint NOT NULL,
                            created_at timestamptz DEFAULT current_timestamp,
                            updated_at timestamptz DEFAULT current_timestamp,

                            CONSTRAINT fk_user_skill_id FOREIGN KEY (user_id) REFERENCES users (id),
                            CONSTRAINT fk_skill_user_id FOREIGN KEY (skill_id) REFERENCES skill (id)
);

CREATE TABLE user_skill_guarantee (
                                      id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                      user_id BIGINT NOT NULL,
                                      skill_id BIGINT NOT NULL,
                                      guarantor_id BIGINT NOT NULL,

                                      CONSTRAINT fk_user_skill_guarantee_user FOREIGN KEY (user_id) REFERENCES users (id),
                                      CONSTRAINT fk_user_skill_guarantee_skill FOREIGN KEY (skill_id) REFERENCES skill (id),
                                      CONSTRAINT fk_user_skill_guarantee_guarantor FOREIGN KEY (guarantor_id) REFERENCES users (id)
);

CREATE TABLE recommendation (
                                id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                content varchar(4096) NOT NULL,
                                author_id bigint NOT NULL,
                                receiver_id bigint NOT NULL,
                                created_at timestamptz DEFAULT current_timestamp,
                                updated_at timestamptz DEFAULT current_timestamp,

                                CONSTRAINT fk_recommender_id FOREIGN KEY (author_id) REFERENCES users (id),
                                CONSTRAINT fk_receiver_id FOREIGN KEY (receiver_id) REFERENCES users (id)
);

CREATE TABLE skill_offer (
                             id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                             skill_id bigint NOT NULL,
                             recommendation_id bigint NOT NULL,

                             CONSTRAINT fk_skill_offered_id FOREIGN KEY (skill_id) REFERENCES skill (id) ON DELETE CASCADE,
                             CONSTRAINT fk_recommendation_skill_id FOREIGN KEY (recommendation_id) REFERENCES recommendation (id) ON DELETE CASCADE
);

CREATE TABLE recommendation_request (
                                        id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                        message varchar(4096) NOT NULL,
                                        requester_id bigint NOT NULL,
                                        receiver_id bigint NOT NULL,
                                        status smallint DEFAULT 0 NOT NULL,
                                        rejection_reason varchar(4096),
                                        recommendation_id bigint,
                                        created_at timestamptz DEFAULT current_timestamp,
                                        updated_at timestamptz DEFAULT current_timestamp,

                                        CONSTRAINT fk_requester_recommendation_id FOREIGN KEY (requester_id) REFERENCES users (id),
                                        CONSTRAINT fk_receiver_recommendation_id FOREIGN KEY (receiver_id) REFERENCES users (id),
                                        CONSTRAINT fk_recommendation_req_id FOREIGN KEY (recommendation_id) REFERENCES recommendation (id) ON DELETE CASCADE
);

CREATE TABLE skill_request (
                               id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                               request_id bigint NOT NULL,
                               skill_id bigint NOT NULL,

                               CONSTRAINT fk_request_skill_id FOREIGN KEY (request_id) REFERENCES recommendation_request (id) ON DELETE CASCADE,
                               CONSTRAINT fk_skill_request_id FOREIGN KEY (skill_id) REFERENCES skill (id)
);

CREATE TABLE contact (
                         id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                         user_id bigint NOT NULL,
                         contact varchar(128) NOT NULL UNIQUE,
                         type smallint NOT NULL,

                         CONSTRAINT fk_contact_owner_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE project_subscription (
                                      id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                      project_id bigint NOT NULL,
                                      follower_id bigint NOT NULL,
                                      created_at timestamptz DEFAULT current_timestamp,
                                      updated_at timestamptz DEFAULT current_timestamp,

                                      CONSTRAINT fk_project_follower_id FOREIGN KEY (follower_id) REFERENCES users (id)
);

CREATE TABLE event (
                       id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                       title varchar(64) NOT NULL,
                       description varchar(4096) NOT NULL,
                       start_date timestamptz NOT NULL,
                       end_date timestamptz NOT NULL,
                       location varchar(128) NOT NULL,
                       max_attendees int,
                       user_id bigint NOT NULL,
                       type smallint NOT NULL,
                       status smallint NOT NULL DEFAULT 0,
                       created_at timestamptz DEFAULT current_timestamp,
                       updated_at timestamptz DEFAULT current_timestamp,

                       CONSTRAINT fk_event_owner_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE event_skill (
                             id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                             event_id bigint NOT NULL,
                             skill_id bigint NOT NULL,

                             CONSTRAINT fk_event_skill_id FOREIGN KEY (event_id) REFERENCES event (id) ON DELETE CASCADE,
                             CONSTRAINT fk_skill_event_id FOREIGN KEY (skill_id) REFERENCES skill (id)
);

CREATE TABLE user_event (
                            id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                            user_id bigint NOT NULL,
                            event_id bigint NOT NULL,

                            CONSTRAINT fk_user_event_id FOREIGN KEY (user_id) REFERENCES users (id),
                            CONSTRAINT fk_event_user_id FOREIGN KEY (event_id) REFERENCES event (id)
);

CREATE TABLE rating (
                        id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                        user_id bigint NOT NULL,
                        event_id bigint NOT NULL,
                        rate smallint NOT NULL,
                        comment varchar(4096),
                        created_at timestamptz DEFAULT current_timestamp,
                        updated_at timestamptz DEFAULT current_timestamp,

                        CONSTRAINT fk_rater_id FOREIGN KEY (user_id) REFERENCES users (id),
                        CONSTRAINT fk_event_rated_id FOREIGN KEY (event_id) REFERENCES event (id)
);

ALTER TABLE users
    ADD COLUMN if not exists profile_pic_file_id text,
    ADD COLUMN if not exists profile_pic_small_file_id text;

CREATE TABLE if not exists content_data (
                                            id bigint PRIMARY key GENERATED ALWAYS AS IDENTITY UNIQUE,
                                            content oid
);

CREATE TABLE contact_preferences (
                                     id bigint PRIMARY key GENERATED ALWAYS AS IDENTITY UNIQUE,
                                     user_id bigint NOT NULL,
                                     preference smallint NOT NULL,

                                     CONSTRAINT fk_contact_preferences_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE goal (
                      id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                      title varchar(64) NOT NULL,
                      description varchar(4096) NOT NULL,
                      parent_goal_id bigint,
                      status smallint DEFAULT 0 NOT NULL,
                      deadline timestamptz,
                      created_at timestamptz DEFAULT current_timestamp,
                      updated_at timestamptz DEFAULT current_timestamp,
                      mentor_id bigint,

                      CONSTRAINT fk_goal_id FOREIGN KEY (parent_goal_id) REFERENCES goal (id),
                      CONSTRAINT fk_mentor_id FOREIGN KEY (mentor_id) REFERENCES users (id)
);

CREATE TABLE goal_invitation (
                                 id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                 goal_id bigint NOT NULL,
                                 inviter_id bigint NOT NULL,
                                 invited_id bigint NOT NULL,
                                 status smallint DEFAULT 0 NOT NULL,
                                 created_at timestamptz DEFAULT current_timestamp,
                                 updated_at timestamptz DEFAULT current_timestamp,

                                 CONSTRAINT fk_inviter_id FOREIGN KEY (inviter_id) REFERENCES users (id),
                                 CONSTRAINT fk_invited_id FOREIGN KEY (invited_id) REFERENCES users (id),
                                 CONSTRAINT fk_goal_id FOREIGN KEY (goal_id) REFERENCES goal (id)
);

CREATE TABLE user_goal (
                           id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                           user_id bigint NOT NULL,
                           goal_id bigint NOT NULL,
                           created_at timestamptz DEFAULT current_timestamp,
                           updated_at timestamptz DEFAULT current_timestamp,

                           CONSTRAINT fk_user_goal_id FOREIGN KEY (user_id) REFERENCES users (id),
                           CONSTRAINT fk_goal_user_id FOREIGN KEY (goal_id) REFERENCES goal (id)
);

CREATE TABLE goal_skill (
                            id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                            goal_id bigint NOT NULL,
                            skill_id bigint NOT NULL,
                            created_at timestamptz DEFAULT current_timestamp,
                            updated_at timestamptz DEFAULT current_timestamp,

                            CONSTRAINT fk_goal_skill_id FOREIGN KEY (goal_id) REFERENCES goal (id),
                            CONSTRAINT fk_skill_goal_id FOREIGN KEY (skill_id) REFERENCES skill (id)
);

INSERT INTO country (title)
VALUES
    ('United States'),
    ('United Kingdom'),
    ('Australia'),
    ('France');

INSERT INTO users (username, email, phone, password, active, about_me, country_id, city, experience, created_at, updated_at)
VALUES
    ('JohnDoe', 'johndoe@example.com', '1234567890', 'password1', true, 'About John Doe', 1, 'New York', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('JaneSmith', 'janesmith@example.com', '0987654321', 'password2', true, 'About Jane Smith', 2, 'London', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('MichaelJohnson', 'michaeljohnson@example.com', '1112223333', 'password3', true, 'About Michael Johnson', 1, 'Sydney', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('EmilyDavis', 'emilydavis@example.com', '4445556666', 'password4', true, 'About Emily Davis', 3, 'Paris', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('WilliamTaylor', 'williamtaylor@example.com', '7778889999', 'password5', true, 'About William Taylor', 2, 'Toronto', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('OliviaAnderson', 'oliviaanderson@example.com', '0001112222', 'password6', true, 'About Olivia Anderson', 1, 'Berlin', 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('JamesWilson', 'jameswilson@example.com', '3334445555', 'password7', true, 'About James Wilson', 3, 'Tokyo', 14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SophiaMartin', 'sophiamartin@example.com', '6667778888', 'password8', true, 'About Sophia Martin', 4, 'Rome', 16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('BenjaminThompson', 'benjaminthompson@example.com', '9990001111', 'password9', true, 'About Benjamin Thompson', 4, 'Moscow', 18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('AvaHarris', 'avaharris@example.com', '2223334444', 'password10', true, 'About Ava Harris', 3, 'Madrid', 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

CREATE TABLE user_premium (
                              id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                              user_id bigint NOT NULL,
                              start_date timestamptz NOT NULL DEFAULT current_timestamp,
                              end_date timestamptz NOT NULL,

                              CONSTRAINT fk_user_premium_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS project
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(128) NOT NULL,
    description      VARCHAR(4096),
    parent_project_id        BIGINT,
    storage_size     BIGINT,
    max_storage_size BIGINT,
    owner_id         BIGINT,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status           VARCHAR(255) NOT NULL,
    visibility       VARCHAR(255) NOT NULL,
    cover_image_id   VARCHAR(255),
    CONSTRAINT fk_project_parent FOREIGN KEY (parent_project_id) REFERENCES project (id)
    );

CREATE TABLE IF NOT EXISTS team
(
    id         BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    CONSTRAINT fk_team_project FOREIGN KEY (project_id) REFERENCES project (id)
    );

CREATE TABLE IF NOT EXISTS team_member
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    CONSTRAINT fk_team_member_team FOREIGN KEY (team_id) REFERENCES team (id)
    );

create table if not exists team_member_roles
(
    team_member_id bigint,
    role           varchar(20) NOT NULL,
    CONSTRAINT fk_team_member_roles_team_member FOREIGN KEY (team_member_id) REFERENCES team_member (id)
    );

CREATE TABLE IF NOT EXISTS resource
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    key        VARCHAR(255),
    type       VARCHAR(255),
    status     VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    project_id BIGINT,
    size       BIGINT,
    CONSTRAINT fk_project
    FOREIGN KEY (project_id) REFERENCES project (id)
    );

CREATE TABLE IF NOT EXISTS task (
                                    id BIGSERIAL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(255),
    performer_user_id BIGINT NOT NULL,
    reporter_user_id BIGINT NOT NULL,
    minutes_tracked INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    parent_task_id BIGINT,
    project_id BIGINT,
    stage_id BIGINT,
    CONSTRAINT fk_parent_task
    FOREIGN KEY (parent_task_id) REFERENCES task(id),
    CONSTRAINT fk_project
    FOREIGN KEY (project_id) REFERENCES project(id)
    );

CREATE TABLE IF NOT EXISTS schedule (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    project_id BIGINT,
    CONSTRAINT fk_project
    FOREIGN KEY (project_id) REFERENCES project(id)
    );

create table if not exists project_stage
(
    project_stage_id   bigserial primary key,
    project_stage_name varchar(255) not null,
    project_id         bigint       not null,

    constraint fk_project
    foreign key (project_id) references project (id)

    );

create table if not exists project_stage_roles
(
    id               bigserial primary key,
    role             varchar(20) not null,
    count            int         not null,
    project_stage_id bigint      not null,

    constraint fk_project_stage
    foreign key (project_stage_id) references project_stage (project_stage_id)
    );

create table if not exists project_stage_executors
(
    id          bigserial primary key,
    stage_id    bigint not null,
    executor_id bigint not null,


    constraint fk_project_stage
    foreign key (stage_id) references project_stage (project_stage_id),
    constraint fk_team_member
    foreign key (executor_id) references team_member (id)
    );

create index project_stage_executors_stage_id_idx on project_stage_executors (stage_id);

create table if not exists stage_invitation
(
    id          bigserial primary key,
    stage_id    bigint      not null,
    author      bigint      not null,
    invited     bigint      not null,
    description varchar(255),
    status      varchar(20) not null,

    constraint fk_stage_id
    foreign key (stage_id) references project_stage (project_stage_id),
    constraint fk_author
    foreign key (author) references team_member (id),
    constraint fk_invited
    foreign key (invited) references team_member (id)
    );

create table IF NOT EXISTS donation (
                                        id BIGSERIAL PRIMARY KEY,
                                        payment_number BIGINT,
                                        amount DECIMAL(19, 2),
    donation_time TIMESTAMP,
    currency VARCHAR(10),
    campaign_id BIGINT,
    user_id BIGINT
    );

create TABLE IF NOT EXISTS campaign (
                                        id BIGSERIAL PRIMARY KEY,
                                        title VARCHAR(255) NOT NULL,
    description VARCHAR(4096),
    goal DECIMAL(19,2),
    amount_raised DECIMAL(19,2),
    status VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    project_id BIGINT,
    currency VARCHAR(10),
    CONSTRAINT fk_project
    FOREIGN KEY (project_id) REFERENCES project(id),
    CONSTRAINT uc_title_project UNIQUE (title, project_id)
    );

alter table resources
    add column if not exists created_by BIGINT,
    add column if not exists updated_by BIGINT;

CREATE TABLE moment (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255),
                        description TEXT,
                        date TIMESTAMP NOT NULL,
                        image_id VARCHAR(255),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP,
                        created_by BIGINT,
                        updated_by BIGINT
);

CREATE TABLE moment_resource (
                                 moment_id BIGINT,
                                 resource_id BIGINT,
                                 CONSTRAINT moment_resource_pk PRIMARY KEY (moment_id, resource_id),
                                 CONSTRAINT moment_resource_moment_fk FOREIGN KEY (moment_id) REFERENCES moment (id) ON DELETE CASCADE,
                                 CONSTRAINT moment_resource_resource_fk FOREIGN KEY (resource_id) REFERENCES resource (id) ON DELETE CASCADE
);

CREATE TABLE moment_project (
                                moment_id BIGINT,
                                project_id BIGINT,
                                CONSTRAINT moment_project_pk PRIMARY KEY (moment_id, project_id),
                                CONSTRAINT moment_project_moment_fk FOREIGN KEY (moment_id) REFERENCES moment (id) ON DELETE CASCADE,
                                CONSTRAINT moment_project_project_fk FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE
);

CREATE TABLE candidate(
                          id             BIGSERIAL PRIMARY KEY,
                          user_id        BIGINT NOT NULL,
                          resume_doc_key VARCHAR(255),
                          cover_letter   TEXT,
                          candidate_status VARCHAR(30)
);

CREATE TABLE vacancy
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   TEXT         NOT NULL,
    project_id    BIGINT       NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP,
    created_by    BIGINT,
    updated_by    BIGINT,
    status        VARCHAR(50)  NOT NULL,
    salary        DECIMAL,
    work_schedule VARCHAR(255),
    count         INT,
    CONSTRAINT vacancy_project_fk FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE TABLE vacancy_skills
(
    vacancy_id BIGINT,
    skill_id   BIGINT,
    CONSTRAINT fk_vacancy_skills_vacancy FOREIGN KEY (vacancy_id) REFERENCES vacancy (id)
);

CREATE TABLE internship (
                            id BIGSERIAL PRIMARY KEY,
                            project_id BIGINT NOT NULL,
                            mentor_id BIGINT NOT NULL,
                            start_date TIMESTAMP NOT NULL,
                            end_date TIMESTAMP,
                            status VARCHAR(50) NOT NULL,
                            description TEXT,
                            name VARCHAR(255) NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP,
                            created_by BIGINT NOT NULL,
                            updated_by BIGINT,
                            schedule_id BIGINT,
                            CONSTRAINT fk_internship_project FOREIGN KEY (project_id) REFERENCES project (id),
                            CONSTRAINT fk_internship_mentor FOREIGN KEY (mentor_id) REFERENCES team_member (id),
                            CONSTRAINT fk_internship_schedule FOREIGN KEY (schedule_id) REFERENCES schedule (id)
);

CREATE TABLE internship_interns (
                                    internship_id BIGINT,
                                    team_member_id BIGINT,
                                    CONSTRAINT fk_internship_interns_internship FOREIGN KEY (internship_id) REFERENCES internship (id),
                                    CONSTRAINT fk_internship_interns_team_member FOREIGN KEY (team_member_id) REFERENCES team_member (id)
);

ALTER TABLE candidate
    ADD COLUMN vacancy_id BIGINT,
ADD CONSTRAINT fk_vacancy
    FOREIGN KEY (vacancy_id)
    REFERENCES vacancy(id);

CREATE TABLE moment_user (

                             moment_id BIGINT,
                             team_member_id BIGINT,
                             CONSTRAINT moment_user_pk PRIMARY KEY (moment_id, team_member_id),
                             CONSTRAINT user_moment_fk FOREIGN KEY (moment_id) REFERENCES moment (id) ON DELETE CASCADE,
                             CONSTRAINT team_member_fk FOREIGN KEY (team_member_id) REFERENCES team_member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS initiative (
                                          id              BIGSERIAL PRIMARY KEY,
                                          curator_id      BIGINT NOT NULL,
                                          project_id      BIGINT NOT NULL,
                                          name            VARCHAR(64) NOT NULL,
    description     VARCHAR(4096) NOT NULL,
    status          VARCHAR(32) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    CONSTRAINT fk_initiative_curator FOREIGN KEY (curator_id) REFERENCES team_member (id),
    CONSTRAINT fk_initiative_project FOREIGN KEY (project_id) REFERENCES project (id)
    );

CREATE TABLE IF NOT EXISTS initiative_project_stages (
                                                         initiative_id       BIGINT NOT NULL,
                                                         project_stage_id    BIGINT NOT NULL,
                                                         CONSTRAINT fk_initiative_project_stages_initiative FOREIGN KEY (initiative_id) REFERENCES initiative (id),
    CONSTRAINT fk_initiative_project_stages_project_stage FOREIGN KEY (project_stage_id) REFERENCES project_stage (project_stage_id)
    );

CREATE TABLE IF NOT EXISTS initiative_project (
                                                  initiative_id   BIGINT NOT NULL,
                                                  project_id      BIGINT NOT NULL,
                                                  CONSTRAINT fk_initiative_project_stages_initiative FOREIGN KEY (initiative_id) REFERENCES initiative (id),
    CONSTRAINT fk_initiative_project_stages_project FOREIGN KEY (project_id) REFERENCES project (id)
    );

ALTER TABLE moment_resource DROP CONSTRAINT moment_resource_resource_fk;
ALTER TABLE resource RENAME TO project_resource;
ALTER TABLE moment_resource
    ADD CONSTRAINT moment_resource_resource_fk
        FOREIGN KEY (resource_id) REFERENCES project_resource (id) ON DELETE CASCADE;
alter table project_resource
    add column if not exists created_by BIGINT,
    add column if not exists updated_by BIGINT;

CREATE TABLE resource_allowed_roles (
                                        id BIGSERIAL PRIMARY KEY,
                                        resource_id  BIGINT NOT NULL,
                                        role_id VARCHAR(16) NOT NULL,
                                        CONSTRAINT fk_resource_roles FOREIGN KEY (resource_id) REFERENCES project_resource (id)
);

CREATE TABLE IF NOT EXISTS meet (
                                    id BIGSERIAL PRIMARY KEY,
                                    project_id BIGINT NOT NULL,
                                    creator_id BIGINT NOT NULL,
                                    status VARCHAR(16) NOT NULL,
    title VARCHAR(128) NOT NULL,
    description VARCHAR(512) NOT NULL,
    starts_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_meet_project FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_meet_user FOREIGN KEY (creator_id) REFERENCES users (id)
    );

CREATE TABLE IF NOT EXISTS meet_participant (
                                                id BIGSERIAL PRIMARY KEY,
                                                meet_id BIGINT NOT NULL,
                                                user_id BIGINT NOT NULL,
                                                CONSTRAINT fk_participant_meet FOREIGN KEY (meet_id) REFERENCES meet (id),
    CONSTRAINT fk_participant_user FOREIGN KEY (user_id) REFERENCES users (id)
    );

ALTER TABLE team_member
    ADD COLUMN nickname VARCHAR(255) NOT NULL;

ALTER TABLE project
    ADD COLUMN presentation_file_key VARCHAR(255),
ADD COLUMN presentation_generated_at TIMESTAMP;

ALTER TABLE team
    ADD COLUMN avatar_key VARCHAR(255);

ALTER TABLE vacancy
    ADD COLUMN cover_image_key VARCHAR(255),
ADD COLUMN position VARCHAR(50) NOT NULL;

CREATE TABLE project_gallery (
                                 project_id BIGINT NOT NULL,
                                 file_key VARCHAR(255) NOT NULL,
                                 PRIMARY KEY (project_id, file_key),
                                 FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);
