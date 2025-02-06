import requests
from django.http import JsonResponse
from django.views import View
from django.shortcuts import render, redirect
from .url import url

class AlumnoView(View):
    def get(self, request, *args, **kwargs):
        api = "alumnos"
        
        headers = {"Content-Type": "application/json"}
        
        response = requests.get(url+api, headers=headers)
        
        context = {
            "DetailAlumnos": response.json()
        }
        
        return render(request, 'Alumnos.html', context)
    