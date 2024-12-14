import bcrypt

# El hash almacenado
stored_hash = "$2b$12$fq7ynoIbnNVo5QXYXnidCe2zItNMTHJ325d/4.sStj4.WjVxgNO8q"

# La contraseña proporcionada por el usuario
password = "123"  # Cambia esto con la contraseña que quieras verificar

# Verificar si la contraseña proporcionada coincide con el hash almacenado
if bcrypt.checkpw(password.encode('utf-8'), stored_hash.encode('utf-8')):
    print("¡La contraseña es correcta!")
else:
    print("La contraseña es incorrecta.")


import bcrypt

# Texto que deseas hashear (puede ser una contraseña o cualquier cadena de texto)
text_to_hash = "123"

# Generar el hash con bcrypt
salt = bcrypt.gensalt()  # Genera una "sal" aleatoria
hashed_text = bcrypt.hashpw(text_to_hash.encode('utf-8'), salt)

# Imprimir el hash generado
print("Texto hasheado:", hashed_text.decode('utf-8'))
