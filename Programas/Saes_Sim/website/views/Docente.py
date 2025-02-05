import requests
from django.http import JsonResponse
from django.views import View
from django.shortcuts import render, redirect
from .url import url

class DocenteView(View):
    def get(self, request, *args, **kwargs):
        api = "docentes"
        
        headers = {"Content-Type": "application/json"}
        
        response = requests.get(url+api, headers=headers)
        
        context = {
            "DetailDocentes": response.json()
        }
        
        return render(request, 'Docente.html', context)