from django.contrib import admin
from django.http import JsonResponse
from django.urls import path

from .tasks import demo_task

def home(request):
    return JsonResponse({
        "message": "Django + Celery + Redis is running",
        "endpoints": {
            "home": "/",
            "run_task": "/run-task/"
        }
    })

def run_task(request):
    task = demo_task.delay()
    return JsonResponse({
        "message": "Celery task sent successfully",
        "task_id": task.id
    })

urlpatterns = [
    path("admin/", admin.site.urls),
    path("", home),
    path("run-task/", run_task),
]
