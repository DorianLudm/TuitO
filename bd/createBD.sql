create table IF NOT EXISTS UTILISATEUR(
    idUtilisateur INTEGER PRIMARY KEY,
    pseudo VARCHAR(255),
    password VARCHAR(255)
);

create table IF NOT EXISTS MESSAGES(
    idUtilisateur INTEGER,
    idMessage INTEGER,
    contenuMessage TEXT(255),
    dateEnvoiMessage DATETIME,
    PRIMARY KEY (idUtilisateur, idMessage)
);

create table IF NOT EXISTS LIKES(
    idUtilisateur INTEGER,
    idMessage INTEGER,
    PRIMARY KEY (idUtilisateur, idMessage)
);

create table IF NOT EXISTS FOLLOW(
    idUtilisateur1 INTEGER,
    idUtilisateur2 INTEGER CHECK (idUtilisateur1 != idUtilisateur2),
    PRIMARY KEY (idUtilisateur1, idUtilisateur2)

);

ALTER TABLE MESSAGES ADD CONSTRAINT FOREIGN KEY (idUtilisateur) REFERENCES UTILISATEUR(idUtilisateur);
ALTER TABLE LIKES ADD CONSTRAINT FOREIGN KEY (idUtilisateur) REFERENCES UTILISATEUR(idUtilisateur);
ALTER TABLE LIKES ADD CONSTRAINT FOREIGN KEY (idMessage) REFERENCES MESSAGES(idMessage);
ALTER TABLE FOLLOW ADD CONSTRAINT FOREIGN KEY (idUtilisateur1) REFERENCES UTILISATEUR(idUtilisateur);
ALTER TABLE FOLLOW ADD CONSTRAINT FOREIGN KEY (idUtilisateur2) REFERENCES UTILISATEUR(idUtilisateur);
