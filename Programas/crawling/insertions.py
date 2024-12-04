import mysql.connector

def querys(query, data):
    connection = mysql.connector.connect(
        host="localhost",
        user="root",
        password="n0m3l0",
        database="TT"
    )
    cursor = connection.cursor()
    
    cursor.execute(query, data)
    connection.commit()
    
    cursor.close()
    connection.close()

def save_into_materia(data):
    query = "INSERT INTO Materia VALUES (%s, %s, %s, %s)"
    values = (data['Cod_mat'], data['Materia'], data['Semestre'], data['Carrera'])
    
    querys(query, values)

def save_into_persona(data):
    query = "INSERT INTO Persona VALUES (%s, %s, %s)"
    values = (data['Boleta'], data['Nombre'], data['Apellidos'])
    
    querys(query, values)


def save_into_ETS(data):
    query = "INSERT INTO ETS VALUES (%s, %s, %s)"
    values = (data['Cod_mat'], data['Turno'], data['Plan'])
    
    querys(query, values)

    
def save_into_AlumnoETS(data):
    query = "INSERT INTO Alumno_ETS VALUES (%s, %s)"
    values = (data['Boleta'], data['Cod_mat'])
    
    querys(query, values)

    
def save_into_ProfesorETS(data):
    query = "INSERT INTO Profesor_ETS VALUES (%s, %s)"
    values = (data['Boleta'], data['Cod_mat'])
    
    querys(query, values)