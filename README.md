# SimpleInterceptor

Simpleinceptor is the interception interface tool of Android okhttp client, which is convenient for testing or development and quick problem finding.

![ SimpleInterceptor]（assets/SimpleInterceptor.gif）

![ SimpleInterceptor]（assets/SimpleInterceptorPage.gif）

## Environmental requirements

1. Android 4.1+

2. OkHttp 3.x。

3. androidx

**Warning * *:

> Data generated and stored when using this interceptor may contain sensitive information such as authorization or cookie headers, as well as the content of the request and response principals.

Therefore, it can only be used in the debugging process and can not be published to the online

to configure

```

dependencies {

    debugImplementation 'com.github.smartbackme.SimpleInterceptor:simpleinterceptor-debug:3.0'
    releaseImplementation 'com.github.smartbackme.SimpleInterceptor:simpleinterceptor-release:3.0'

}

```

use:

```

OkHttpClient.Builder()

.addInterceptor(SimpleInterceptor(context))

.build()

```

thank

----------------

Simpleinceptor uses the following open source libraries:

okhttp，gson，cupboard

To adapt the project to the new okhttp, Android x, kotlin,

Project adaptation from[ https://github.com/jgilfelt/chuck ]( https://github.com/jgilfelt/chuck )

License
-------

    Copyright (C) 2017 Jeff Gilfelt.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.