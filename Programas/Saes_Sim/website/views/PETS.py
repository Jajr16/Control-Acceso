import requests
from django.http import JsonResponse
from django.views import View
from django.shortcuts import render, redirect
from .url import url
from ..forms import periodoForm

class PETSView(View):
    def get(self, request, *args, **kwargs):
        api = "periodoETS"
        
        headers = {"Content-Type": "application/json"}
        
        response = requests.get(url+api, headers=headers)
        
        context = {
            "DetailPETS": response.json()
        }
        
        return render(request, 'PETS.html', context)
    
class NPETSView(View):
    def get(self, request, *args, **kwargs):
        form = periodoForm()
        return render(request, 'New_PETS.html', {'form': form  })
    
    def post(self, request, *args, **kwargs):
        api = "periodoETS"
        
        form = periodoForm(request.POST)
        
        if form.is_valid():
            periodo = form.cleaned_data['periodo']
            tipo = form.cleaned_data['tipo']
            fecha_I = form.cleaned_data['fecha_I'].strftime('%Y-%m-%d')
            fecha_F = form.cleaned_data['fecha_F'].strftime('%Y-%m-%d')
            
            data = {
                "idPeriodo": None,
                "periodo": periodo,
                "tipo": tipo[0],
                "fecha_Inicio": fecha_I,
                "fecha_Fin": fecha_F,
            }
            
            print(data)
            
            headers = {"Content-Type": "application/json"}
            response = requests.post(url+api, json=data, headers=headers)
            response_data = response.json() 
            
            if response_data.get("Error"):
                return render(request, 'New_PETS.html', {'form': form, 'message': response_data.get("message")})
            else:
                return render(request, 'New_PETS.html', {'form': form, 'message': response_data.get("message")})
        else:
            print("Error en el formulario:", form.errors)
            return render(request, 'New_PETS.html', {'form': form})