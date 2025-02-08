from django.urls import path
from . import views

urlpatterns = [
    path('', views.LoginView.as_view(), name='login'),
    path('home/', views.HomeView.as_view(), name='home'),
    path('listETS/', views.ListETSView.as_view(), name='listETS'),
    path('Alumno/', views.AlumnoView.as_view(), name='alumno'),
    path('Docente/', views.DocenteView.as_view(), name='docente'),
    path('PS/', views.PSView.as_view(), name='ps'),
    path('PETS/', views.PETSView.as_view(), name='pets'),
    path('NPETS/', views.NPETSView.as_view(), name='npets'),
    path('NETS/', views.NETSView.as_view(), name='nets'),
    path('NPS/', views.NPSView.as_view(), name='nps'),
]