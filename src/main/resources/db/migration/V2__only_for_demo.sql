INSERT INTO users(uuid, "password", email, is_active)
VALUES ('2f94ec31-ad37-4fa4-800e-3c574784af79',
        'pbkdf2_sha256$150000$ns9fEdfOnry6$I1YUZqc9MP6YGoFTYGE4/B4qTHFBSzIMGvdZ9EaTrb8=', 'bgeota@gmail.com', true),
       ('31bb2882-8656-11ee-b9d1-0242ac120002',
        'pbkdf2_sha256$150000$ns9fEdfOnry6$I1YUZqc9MP6YGoFTYGE4/B4qTHFBSzIMGvdZ9EaTrb8=', 'bgeota+1@gmail.com', true);

INSERT INTO roles_user_matcher(role_id, user_id)VALUES (1, 1);

INSERT INTO telegram_user (id, external_id, first_name, last_name, user_name, created_at) VALUES (1, 123456789, 'Oleksii', 'Borodai', 'bgeota', '2023-11-20 14:19:59.066893');
INSERT INTO telegram_user (id, external_id, first_name, last_name, user_name, created_at) VALUES (2, 987654321, 'No', null, 'no name', '2023-11-20 14:21:49.495359');

INSERT INTO chat_dialog (id, telegram_user_id, user_request, bot_response, created_at) VALUES (1, 1, 'Hello', 'Hello, how can I assist you today', '2023-11-20 13:51:16.263955');
INSERT INTO chat_dialog (id, telegram_user_id, user_request, bot_response, created_at) VALUES (2, 1, 'What is you tech name', 'I am developed and maintained by OpenAI, and my name is GPT-3, which stands for \', '2023-11-20 13:54:13.943546');
INSERT INTO chat_dialog (id, telegram_user_id, user_request, bot_response, created_at) VALUES (3, 1, 'please repeate', 'Sure, please repeat what you would like me to do.', '2023-11-20 13:55:13.070404');
INSERT INTO chat_dialog (id, telegram_user_id, user_request, bot_response, created_at) VALUES (4, 1, 'repeat what is you name', 'What is your name?', '2023-11-20 13:55:30.982976');
INSERT INTO chat_dialog (id, telegram_user_id, user_request, bot_response, created_at) VALUES (5, 1, 'What is you tech name', 'I am a language model AI developed by OpenAI called GPT-3.', '2023-11-20 13:55:49.526396');
INSERT INTO chat_dialog (id, telegram_user_id, user_request, bot_response, created_at) VALUES (6, 1, 'give me more detail about project', 'Sure, I''d be happy to provide more details about the project. Can you please specify which project you are referring to? Once I have more information, I can give you a detailed overview of the project, including its objectives, scope, timeline, stakeholders, resources, and any other relevant information.', '2023-11-20 13:56:14.558125');
INSERT INTO chat_dialog (id, telegram_user_id, user_request, bot_response, created_at) VALUES (7, 2, 'Какой сегодня день ?', 'Сегодня пятница.', '2023-11-20 14:27:53.445073');
INSERT INTO chat_dialog (id, telegram_user_id, user_request, bot_response, created_at) VALUES (8, 2, 'Расскажи сказку про колобка', 'Жили-были старик со старухой, и у них была такая колобок. Они его замесили, замесили, и вытащили на окошко греться на солнышке.\n\nИ вот колобок, зашумел, и покатился от окна через поле, солнце его пекло, он катился-катился, и пел: \', '2023-11-20 14:28:47.174145');
INSERT INTO chat_dialog (id, telegram_user_id, user_request, bot_response, created_at) VALUES (9, 1, 'мені тут підказують що сьогодні пʼянтиця, це так?', 'Відповідно до моїх знань, я не можу сказати, що сьогодні п''янтиця. Якщо у вас є питання про ситуацію з алкоголем або споживанням алкоголю, я раджу звернутися до лікаря або професійного консультанта з цієї теми.', '2023-11-20 14:29:27.388643');

SELECT setval(pg_get_serial_sequence('users', 'id'), coalesce(max(id) + 1, 1), false) FROM "users";
SELECT setval(pg_get_serial_sequence('chat_dialog', 'id'), coalesce(max(id) + 1, 1), false) FROM chat_dialog;
SELECT setval(pg_get_serial_sequence('telegram_user', 'id'), coalesce(max(id) + 1, 1), false) FROM telegram_user;
