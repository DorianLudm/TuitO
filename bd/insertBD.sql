insert into UTILISATEUR(idUtilisateur, pseudo, password) values
    (1,'Dorian',123),
    (2,'Adèle',456),
    (3,'Tom',789);

insert into MESSAGES(idUtilisateur, idMessage, contenuMessage, dateEnvoiMessage) values
    (1,1,'Bonjour Adèle','2024-01-09 18:00:00'),
    (2,2,'Bonjour Tom','2024-01-09 19:00:00'),
    (3,3,'Bonjour Dorian','2024-01-09 20:00:00');

insert into LIKES(idUtilisateur, idMessage) values
    (2,1),
    (3,2),
    (1,3);

insert into FOLLOW(idUtilisateur1, idUtilisateur2) values
    (1,2),
    (1,3),
    (2,1),
    (2,3),
    (3,1),
    (3,2);