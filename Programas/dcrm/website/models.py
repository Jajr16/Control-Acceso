from django.contrib.auth.models import AbstractBaseUser, BaseUserManager
from django.db import models
from django.conf import settings

class UsuarioManager(BaseUserManager):
    def create_user(self, boleta, nombre, apellido, password=None):
        if not boleta:
            raise ValueError("El usuario debe tener una boleta")
        user = self.model(boleta=boleta, nombre=nombre, apellido=apellido)
        user.set_password(password) 
        user.save(using=self._db)
        return user

    def create_superuser(self, boleta, nombres, password=None):
        user = self.create_user(boleta, nombres, password)
        user.is_admin = True
        user.save(using=self._db)
        return user

class Usuario(AbstractBaseUser):
    boleta = models.CharField(max_length=10, primary_key=True, unique=True)
    nombre = models.CharField(max_length=100)
    apellido = models.CharField(max_length=100)
    is_active = models.BooleanField(default=True)
    is_admin = models.BooleanField(default=False)

    objects = UsuarioManager()

    USERNAME_FIELD = 'boleta'
    REQUIRED_FIELDS = ['nombre', 'apellido']

    def __str__(self):
        return self.boleta

    def has_perm(self, perm, obj=None):
        return True

    def has_module_perms(self, app_label):
        return True

    @property
    def is_staff(self):
        return self.is_admin

class Materias(models.Model):
    cod_mat = models.CharField(max_length=10, primary_key=True, unique=True)
    materia = models.CharField(max_length=100, null=False)
    carrera = models.CharField(max_length=100, null=False)

class ETS(models.Model):
    TURNOS = [
        ('MAT', 'Matutino'),
        ('VES', 'Vespertino')
    ]

    PERIODO_ESCOLAR = [
        ('2023-1', '2023 Primer Semestre'),
        ('2023-2', '2023 Segundo Semestre'),
        ('2024-1', '2024 Primer Semestre'),
    ]
    
    cod_ets = models.CharField(max_length=10, primary_key=True, unique=True)
    cod_mat = models.ForeignKey(Materias, on_delete=models.CASCADE)
    turno = models.CharField(max_length=3, choices=TURNOS, default='MAT')
    periodo_escolar = models.CharField(max_length=10, choices=PERIODO_ESCOLAR, default='2024-1')

    
class Alumno_ETS(models.Model):
    cod_profe = models.ForeignKey(settings.AUTH_USER_MODEL,limit_choices_to={'is_admin': True}, on_delete=models.CASCADE, related_name='profesor')
    boletaAlm = models.ForeignKey(settings.AUTH_USER_MODEL, limit_choices_to={'is_admin': False}, on_delete=models.CASCADE, related_name='alumno')
    cod_ETS = models.ForeignKey(ETS, on_delete=models.CASCADE)
    
    class Meta:
        constraints = [
            models.UniqueConstraint(fields=['cod_profe', 'boletaAlm', 'cod_ETS'], name='Alumn_Ets')
        ]
    