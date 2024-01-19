insert into UTILISATEUR(idUtilisateur, pseudo, password) values
    (10, 'Adèle', '1234'),
    (11, 'Tom', '1234'),
    (12, 'Dorian', '1234'),
    (13, 'Léa', '1234'),
    (14, 'Léo', '1234'),
    (15, 'Lola', '1234'),
    (16, 'Lucas', '1234'),
    (17, 'Manon', '1234'),
    (18, 'Mathis', '1234'),
    (19, 'Noémie', '1234'),
    (20, 'Paul', '1234'),
    (21, 'Sarah', '1234'),
    (22, 'Théo', '1234'),
    (23, 'Zoé', '1234');

insert into MESSAGES(idUtilisateur, idMessage, contenuMessage, dateEnvoiMessage) values
    (1,1,'Bonjour Adèle','2024-01-09 18:00:00'),
    (2,2,'Bonjour Tom','2024-01-09 19:00:00'),
    (3,3,'Bonjour Dorian','2024-01-09 20:00:00');

insert into LIKES(idUtilisateur, idMessage) values
    (2,1),
    (3,2),
    (1,3);

insert into FOLLOW(idUtilisateur1, idUtilisateur2) values
    (1,16),
    (1,17),
    (1,18),
    (1,19),
    (1,20),
    (1,21),
    (1,22),
    (1,23);