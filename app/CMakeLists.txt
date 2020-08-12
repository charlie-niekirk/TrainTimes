# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.
#set(CMAKE_C_COMPILER_WORKS TRUE)
#set(CMAKE_CXX_COMPILER_WORKS TRUE)

set(can_use_assembler TRUE)
enable_language(ASM)

include_directories(C:/Users/cniek/AppData/Local/Android/Sdk/ndk/16.1.4479499/sysroot/usr/include)

add_library( # Specifies the name of the library.
             contour

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/contour.cpp)

find_library( # Defines the name of the path variable that stores the
        # location of the NDK library.
        log-lib

        # Specifies the name of the NDK library that
        # CMake needs to locate.
        log )

#set(OBFUSCATOR_FLAGS "-fvisibility=hidden -mllvm -bcf -mllvm -bcf_prob=100 -mllvm -bcf_loop=1 -mllvm -sub_loop=3 -mllvm -fla=1 -mllvm -sobf")
#set(OBFUSCATOR_FLAGS "-fvisibility=hidden -mllvm -sub_loop=3 -mllvm -fla=1 -mllvm -sobf")
set(OBFUSCATOR_FLAGS "-fvisibility=hidden")
string(APPEND CMAKE_CXX_FLAGS " ${OBFUSCATOR_FLAGS}")

include_directories(src/main/jniLibs/armeabi-v7a/include/)

target_link_libraries( # Specifies the target library.
        contour
        # Links the log library to the target library.
        ${log-lib})

