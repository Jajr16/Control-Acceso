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
        return render(request, 'New_ETS.html', {'form': form  })
    
    # def post(self, request, *args, **kwargs):