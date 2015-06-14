"""
WSGI config for sysu_library project.

It exposes the WSGI callable as a module-level variable named ``application``.

For more information on this file, see
https://docs.djangoproject.com/en/1.6/howto/deployment/wsgi/
"""

import os
import sys
#path = '/home/yzkk/yzkk/git/sysu_library_assistant/sysu_library'
#if path not in sys.path:
#    sys.path.insert(0, path + '/sysu_library')
os.environ.setdefault("DJANGO_SETTINGS_MODULE", "sysu_library.settings")

from django.core.wsgi import get_wsgi_application
application = get_wsgi_application()
