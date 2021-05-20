![](https://img.shields.io/badge/JitPack-4.0-green)
![](https://img.shields.io/badge/JitPack-3.0-green)
![](https://img.shields.io/badge/code-kotlin-red)
![](https://img.shields.io/badge/Android%20Arsenal-SimpleInterceptor-yellow)
![](https://img.shields.io/badge/author-smartbackme-blue)

# SimpleInterceptor

Simpleinceptor is the interception interface tool of Android okhttp client, which is convenient for testing or development and quick problem finding.

![SimpleInterceptor](assets/SimpleInterceptor.gif)
![SimpleInterceptor](assets/SimpleInterceptorPage.gif)

## Environmental requirements

1. Android 4.1+

2. OkHttp 3.x or 4.x

3. androidx

**Warning * *:

> Data generated and stored when using this interceptor may contain sensitive information such as authorization or cookie headers, as well as the content of the request and response principals.

Therefore, it can only be used in the debugging process and can not be published to the online

to configure

Version associated with okhttp:

If the app is integrated with okhttp3. + version, please choose version 3.0 code

If the app is integrated with okhttp3 4. + version, please choose version 4.0 code

```
okhttp3 3.+
dependencies {

    debugImplementation 'com.github.smartbackme.SimpleInterceptor:simpleinterceptor-debug:3.0'
    releaseImplementation 'com.github.smartbackme.SimpleInterceptor:simpleinterceptor-release:3.0'
}
or

okhttp3 4.+
dependencies {

    debugImplementation 'com.github.smartbackme.SimpleInterceptor:simpleinterceptor-debug:4.0'
    releaseImplementation 'com.github.smartbackme.SimpleInterceptor:simpleinterceptor-release:4.0'
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