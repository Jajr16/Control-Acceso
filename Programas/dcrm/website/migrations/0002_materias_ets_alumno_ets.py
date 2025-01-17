# Generated by Django 5.1 on 2024-09-21 22:44

import django.db.models.deletion
from django.conf import settings
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('website', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Materias',
            fields=[
                ('cod_mat', models.CharField(max_length=10, primary_key=True, serialize=False, unique=True)),
                ('materia', models.CharField(max_length=100)),
                ('carrera', models.CharField(max_length=100)),
            ],
        ),
        migrations.CreateModel(
            name='ETS',
            fields=[
                ('cod_ets', models.CharField(max_length=10, primary_key=True, serialize=False, unique=True)),
                ('turno', models.CharField(choices=[('MAT', 'Matutino'), ('VES', 'Vespertino')], default='MAT', max_length=3)),
                ('periodo_escolar', models.CharField(choices=[('2023-1', '2023 Primer Semestre'), ('2023-2', '2023 Segundo Semestre'), ('2024-1', '2024 Primer Semestre')], default='2024-1', max_length=10)),
                ('cod_mat', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='website.materias')),
            ],
        ),
        migrations.CreateModel(
            name='Alumno_ETS',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('boletaAlm', models.ForeignKey(limit_choices_to={'is_admin': False}, on_delete=django.db.models.deletion.CASCADE, related_name='alumno', to=settings.AUTH_USER_MODEL)),
                ('cod_profe', models.ForeignKey(limit_choices_to={'is_admin': True}, on_delete=django.db.models.deletion.CASCADE, related_name='profesor', to=settings.AUTH_USER_MODEL)),
                ('cod_ETS', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='website.ets')),
            ],
            options={
                'constraints': [models.UniqueConstraint(fields=('cod_profe', 'boletaAlm', 'cod_ETS'), name='Alumn_Ets')],
            },
        ),
    ]
