create database if not exists TT;
use TT;

create table Materia(
	Cod_mat varchar(10) not null,
	Materia varchar(100),
	Semestre int,
    Carrera varchar(5),
	primary key (Cod_mat) 
);

create table Persona(
	Boleta varchar(50) not null,
	Nombre varchar(300),
	primary key (Boleta)
);

create table ETS(
	Cod_mat varchar(10) not null,
    Turno char,
    Plan varchar(10),
    primary key (Cod_mat),
    foreign key (Cod_mat) references Materia(Cod_mat)
    ON DELETE CASCADE
	ON UPDATE CASCADE
);

create table Alumno_ETS(
	Boleta varchar(50) not null,
    Cod_mat varchar(10) not null,
    primary key (Boleta, Cod_mat),
    foreign key (Boleta) references Persona(Boleta),
    foreign key (Cod_mat) references Materia(Cod_mat)
);

create table Profesor_ETS(
	Boleta varchar(50) not null,
    Cod_mat varchar(10) not null,
    primary key (Boleta, Cod_mat),
    foreign key (Boleta) references Persona(Boleta),
    foreign key (Cod_mat) references Materia(Cod_mat)
);