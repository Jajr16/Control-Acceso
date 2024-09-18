import mysql.connector

database = mysql.connector.connect(
    host = 'localhost',
    user = 'root',
    passwd = 'n0m3l0'
)

cursorObject = database.cursor()

cursorObject.execute('CREATE DATABASE RFTT')
print('Hecho')