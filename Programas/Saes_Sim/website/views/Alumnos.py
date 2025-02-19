import requests
from django.http import JsonResponse
from django.views import View
from django.shortcuts import render, redirect
from .url import url
from ..forms import NAlumnoForm
from django.core.files.storage import default_storage
from django.core.files.base import ContentFile
import json

def carreras(request):
    escuela = request.GET.get("escuela")
    
    api = "programasAcademicos"
    
    if not escuela:
        return JsonResponse({"error": "Falta el parámetro 'escuela'"}, status=400)

    try:
        escuela = int(escuela)
    except ValueError:
        return JsonResponse({"error": "El parámetro 'escuela' debe ser un número"}, status=400)

    headers = {"Content-Type": "application/json"}
    data = json.dumps({"escuela": escuela}) 

    try:
        response = requests.post(url+api, data=data, headers=headers)
        response.raise_for_status()
        response_data = response.json()
    except requests.exceptions.RequestException as e:
        return JsonResponse({"error": f"Error al conectar con la API: {str(e)}"}, status=500)

    return JsonResponse({"carreras": list(response_data)})

class AlumnoView(View):
    """
        Clase que define la vista del listado de los alumnos registrados
    """
    def get(self, request, *args, **kwargs):
        """
            Función get de la vista encargada de renderizar la página html
        """

        api = "alumnos"
        
        headers = {"Content-Type": "application/json"}
        
        response = requests.get(url+api, headers=headers)
        
        context = {
            "DetailAlumnos": response.json()
        }
        
        return render(request, 'Alumnos.html', context)
    
class NAlumnoView(View):
    """
        Clase que define la vista del formulario de alta de Alumnos
    """
    def get(self, request, *args, **kwargs):
        """
            Función para cargar la vista de la página html con el formulario
        """
        form = NAlumnoForm()
        return render(request, "New_Alumno.html", { 'form': form })
    
    def post(self, request, *args, **kwargs):
        """
            Función post para procesar los datos enviados del formulario
        """
        
        print(request)
        
        api = "nAlumno"
        form = NAlumnoForm(request.POST)
        
        if (form.is_valid()):
            curp = form.cleaned_data['curp']
            boleta = form.cleaned_data['boleta']
            nombre = form.cleaned_data['nombre']
            apellido_P = form.cleaned_data['apellido_P']
            apellido_M = form.cleaned_data['apellido_M']
            sexo = form.cleaned_data['sexo']
            correo = form.cleaned_data['correo']
            escuela = form.cleaned_data['escuela']
            carrera = form.cleaned_data['carrera']
            
            credencial = request.FILES.get("foto-file")
            video = request.FILES.get("video-file")
            
            if not video:
                return render(request, 'New_Alumno.html', {'form': form, 'message': "El video es obligatorio", 'Error': True})
            
            if credencial:
                with open(f"website/views/fotos/{boleta}.jpg", "wb") as f:
                    for chunk in credencial.chunks():
                        f.write(chunk)
                        
            foto_path = f"website/views/fotos/{boleta}.jpg" if credencial else None
            
            files = {'video': video}
            data = {
                "curp": curp,
                "boleta": boleta,
                "nombre": nombre,
                "apellido_p": apellido_P,
                "apellido_m": apellido_M,
                "sexo": sexo,
                "correo": correo,
                "escuela": int(escuela),
                "carrera": carrera,
                "credencial": foto_path
            }
            
            response = requests.post(url+api, data=data, files=files)
            response_data = response.json()
            
            print(response_data)
            
            if response_data.get("Error"):
                return JsonResponse(response_data, status=400)
            else:
                return JsonResponse(response_data, status=200)
        
        else:
            return JsonResponse({"message": "Error en el formulario", "Error": True}, status=400)