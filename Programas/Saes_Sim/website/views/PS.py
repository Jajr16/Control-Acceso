import requests
from django.http import JsonResponse
from django.views import View
from django.shortcuts import render, redirect
from .url import url
from ..forms import NPSForm

class PSView(View):
    def get(self, request, *args, **kwargs):
        api = "ps"
        
        headers = {"Content-Type": "application/json"}
        
        response = requests.get(url+api, headers=headers)
        
        context = {
            "DetailPS": response.json()
        }
        
        return render(request, 'PS.html', context)
    
class NPSView(View):
    def get(self, request, *args, **kwargs):
        form = NPSForm()
        return render(request, 'New_PersonalSeguridad.html', {'form': form  })
    
    def post(self, request, *args, **kwargs):
        api = "NPS"
        
        form = NPSForm(request.POST)
        
        if form.is_valid():
            curp = form.cleaned_data['curp']
            nombre = form.cleaned_data['nombre']
            apellido_P = form.cleaned_data['apellido_P']
            apellido_M = form.cleaned_data['apellido_M']
            sexo = form.cleaned_data['sexo']
            cargoPS = form.cleaned_data['cargoPS']
            turno = form.cleaned_data['turno']
            
            data = {
                "curp": curp,
                "nombre": nombre,
                "apellido_P": apellido_P,
                "apellido_M": apellido_M,
                "sexo": sexo,
                "cargoPS": cargoPS,
                "turno": turno
            }
            
            headers = {"Content-Type": "application/json"}
            response = requests.post(url+api, json=data, headers=headers)
            response_data = response.json()
            
            print(response_data)
            
            if response_data.get("Error"):
                return render(request, 'New_PersonalSeguridad.html', {'form': form, 'message': response_data.get("message")})
            else:
                return render(request, 'New_PersonalSeguridad.html', {'form': form, 'message': response_data.get("message")})
        else:
            print("Error en el formulario:", form.errors)
            return render(request, 'New_PersonalSeguridad.html', {'form': form})