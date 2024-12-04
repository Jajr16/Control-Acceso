from django.shortcuts import render, redirect
from .forms import LoginForm
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from .forms import RegistroForm
from django.shortcuts import get_object_or_404
from .models import ETS
from .models import Alumno_ETS
from .models import Materias

def login_view(request):
    if request.method == 'POST':
        form = LoginForm(request.POST)
        if form.is_valid():
            
            boleta = form.cleaned_data['boleta']
            contraseña = form.cleaned_data['contraseña']
            print(contraseña)
            print(boleta)
            
            user = authenticate(request, username=boleta, password=contraseña)
            
            if user is not None:
                login(request, user)
                return redirect('home')
            else:
                messages.error(request, 'Usuario o contraseña incorrectos')
    else:
        form = LoginForm()

    return render(request, 'home.html', {'form': form})

@login_required
def home_view(request):
    resultados = ETS.objects.select_related('cod_mat').all()
    
    datos_ets = []
    for resultado in resultados:
        datos_ets.append({
            'carrera': resultado.cod_mat.carrera,
            'periodo': resultado.periodo_escolar,
            'no_materia': resultado.cod_mat.cod_mat,
            'materia': resultado.cod_mat.materia,
            'turno': resultado.turno
        })
        
    context = {
        'asignaciones': datos_ets
    }
    return render(request, 'success.html', context)

@login_required
def alumnos_ets(request, codigo_materia):
    ets = get_object_or_404(ETS, cod_mat=codigo_materia)
    
    print(ets.__dict__)
    materia = Materias.objects.get(cod_mat=ets.cod_mat_id)
    
    alumnos_ets = Alumno_ETS.objects.filter(cod_ETS=ets).select_related('boletaAlm', 'cod_ETS__cod_mat')
    
    # for alumno in alumnos_ets:
    #     print(vars(alumno))
    
    context = {
        'materia': materia.materia,
        'alumnos': alumnos_ets 
    }
    
    return render(request, 'ETS.html', context)

def register_view(request):
    if request.method == 'POST':
        form = RegistroForm(request.POST)
        if form.is_valid():
            usuario = form.save(commit=False)
            usuario.set_password(form.cleaned_data['password'])
            usuario.save()
            login(request, usuario)  # Autentica e inicia sesión automáticamente
            messages.success(request, 'Usuario registrado con éxito')
            return redirect('home')  # Redirige al home o a donde desees
        else:
            messages.error(request, 'Error en el registro')
    else:
        form = RegistroForm()
    
    return render(request, 'register.html', {'form': form})

def photos(request):
    return render(request, 'fotos.html')
