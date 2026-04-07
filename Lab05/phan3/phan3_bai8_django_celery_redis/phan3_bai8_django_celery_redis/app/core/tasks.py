import time
from celery import shared_task

@shared_task
def demo_task():
    print("Starting demo task...")
    time.sleep(5)
    print("Demo task completed.")
    return "Task finished successfully"
