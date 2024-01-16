create table IF NOT EXISTS UTILISATEUR(
    idUtilisateur INTEGER PRIMARY KEY,
    pseudo VARCHAR(255),
    password VARCHAR(255)
);

create table IF NOT EXISTS MESSAGES(
    idMessage INTEGER PRIMARY KEY AUTO_INCREMENT,
    idUtilisateur INTEGER,
    contenuMessage TEXT(255),
    dateEnvoiMessage DATETIME,
    FOREIGN KEY (idUtilisateur) REFERENCES UTILISATEUR(idUtilisateur)
);

create table IF NOT EXISTS LIKES(
    idUtilisateur INTEGER,
    idMessage INTEGER,
    PRIMARY KEY (idUtilisateur, idMessage),
    FOREIGN KEY (idUtilisateur) REFERENCES UTILISATEUR(idUtilisateur),
    FOREIGN KEY (idMessage) REFERENCES MESSAGES(idMessage)
);

create table IF NOT EXISTS FOLLOW(
    idUtilisateur1 INTEGER,
    idUtilisateur2 INTEGER CHECK (idUtilisateur1 != idUtilisateur2),
    PRIMARY KEY (idUtilisateur1, idUtilisateur2),
    FOREIGN KEY (idUtilisateur1) REFERENCES UTILISATEUR(idUtilisateur),
    FOREIGN KEY (idUtilisateur2) REFERENCES UTILISATEUR(idUtilisateur)
);