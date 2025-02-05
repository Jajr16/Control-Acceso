import requests
from django.http import JsonResponse
from django.views import View
from django.shortcuts import render, redirect
from .url import url
from ..forms import ETSForm

class ListETSView(View):
    def get(self, request, *args, **kwargs):
        api = "ets"
        
        headers = {"Content-Type": "application/json"}
        
        response = requests.get(url+api, headers=headers)
        
        context = {
            'DetallesETS': response.json()
        }
        
        return render(request, 'ETS.html', context)
    
class NETSView(View):
    def get(self, request, *args, **kwargs):
        form = ETSForm()
        return render(request, 'New_ETS.html', {'form': form  })
    
    def post(self, request, *args, **kwargs):
        api = "NETS"
        
        form = ETSForm(request.POST)
        
        if form.is_valid():
            idPeriodo = int(form.cleaned_data['idPeriodo'])
            turno = form.cleaned_data['Turno']
            fecha = form.cleaned_data['Fecha'].strftime('%Y-%m-%d')
            cupo = form.cleaned_data['Cupo']
            idUA = form.cleaned_data['idUA']
            duracion = form.cleaned_data['Duracion']
            
            data = {
                "idPeriodo": idPeriodo,
                "turno": turno,
                "fecha": fecha,
                "cupo": cupo,
                "idUA": idUA,
                "duracion": duracion,
            }
            
            print(data)
            
            headers = {"Content-Type": "application/json"}
            response = requests.post(url+api, json=data, headers=headers)
            response_data = response.json() 
            
            if response_data.get("Error"):
                return render(request, 'New_ETS.html', {'form': form, 'message': response_data.get("message")})
            else:
                return render(request, 'New_ETS.html', {'form': form, 'message': response_data.get("message")})
        else:
            print("Error en el formulario:", form.errors)
            return render(request, 'New_ETS.html', {'form': form})