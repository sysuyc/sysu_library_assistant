from django.conf.urls import patterns, include, url
from library.views import *

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'rank.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
    url(r'^course/', searchByCourse),
    url(r'^book/', searchByBook),
    url(r'^detail/', getBookDetail),
    url(r'^click/', clickIncrement),
)
