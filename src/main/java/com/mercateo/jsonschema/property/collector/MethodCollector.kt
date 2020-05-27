package com.mercateo.jsonschema.property.collector

import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.property.RawProperty
import com.mercateo.jsonschema.property.RawPropertyCollector
import com.mercateo.jsonschema.property.annotation.AnnotationProcessor
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

class MethodCollector(
    private val annotationProcessor: AnnotationProcessor = AnnotationProcessor()
) : RawPropertyCollector {

    override fun <S> forType(genericType: GenericType<S>): Sequence<RawProperty<S, *>> {
        val declaredMethods = genericType.declaredMethods
        return sequenceOf(*declaredMethods)
            .filter { !it.isSynthetic }
            .filter { it.declaringClass != Any::class.java }
            .filter { it.returnType != Void.TYPE }
            .filter { !it.overridesObject }
            .filter { it.parameterCount == 0 }
            .filter { !Modifier.isStatic(it.modifiers) }
            .map { mapRawDataProperty(method = it, genericType = genericType) }
    }

    private fun <S> mapRawDataProperty(method: Method, genericType: GenericType<S>): RawProperty<S, *> {
        val methodType = GenericType.ofMethod(method, genericType.type)
        return RawProperty(
            getPropertyName(method),
            methodType,
            annotationProcessor.collectAndGroup(collectAnnotations(method))
        ) { instance: S -> valueAccessor(method, instance) }
    }

    private fun getPropertyName(method: Method): String {
        val methodName = method.name
        if (methodName.startsWith("get") && Character.isUpperCase(methodName[3])) {
            return Character.toLowerCase(methodName[3]) + methodName.substring(4)
        }
        if (methodName.startsWith("is") && Character.isUpperCase(methodName[2])) {
            return Character.toLowerCase(methodName[2]) + methodName.substring(3)
        }
        return methodName
    }

    private fun <S> valueAccessor(method: Method, instance: S): Any? {
        return method.invoke(instance)
    }

    private fun collectAnnotations(rootMethod: Method): Set<Annotation> {
        val annotations: MutableSet<Annotation> = mutableSetOf()

        val stack: Stack<Method> = Stack()
        stack.push(rootMethod)

        while (stack.isNotEmpty()) {
            val method = stack.pop()

            annotations.addAll(method.annotations)

            addSuperclassMethod(method.declaringClass, method, stack)
            addInterfaceMethods(method.declaringClass, method, stack)
        }
        return annotations
    }


    private fun addInterfaceMethods(declaringClass: Class<*>, method: Method, stack: Stack<Method>) {
        val interfaces = declaringClass.interfaces
        for (`interface` in interfaces) {
            try {
                val interfaceMethod = `interface`.getDeclaredMethod(method.name, *method.parameterTypes)
                stack.push(interfaceMethod)
            } catch (e: NoSuchMethodException) {
            }
        }
    }

    private fun addSuperclassMethod(declaringClass: Class<*>, method: Method, stack: Stack<Method>) {
        val superclass = declaringClass.superclass
        if (superclass != null && superclass != Object::class.java) {
            try {
                val superMethod = superclass.getDeclaredMethod(method.name, *method.parameterTypes)
                stack.push(superMethod)
            } catch (e: NoSuchMethodException) {
            }
        }
    }
}

private val Method.overridesObject: Boolean
    get() {
        var clazz: Class<*>? = this.declaringClass

        while (clazz != null) {
            val overridesMethod = clazz.declaredMethods.filter { it.name == this.name }
                .any { Arrays.equals(it.parameterTypes, this.parameterTypes) }

            if (overridesMethod && clazz == Object::class.java) {
                return true
            }
            clazz = clazz.superclass
        }
        return false
    }

