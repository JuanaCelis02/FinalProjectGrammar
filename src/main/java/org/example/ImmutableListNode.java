package org.example;

import java.util.Iterator;

/**
 * Implementación de una lista enlazada inmutable.
 * 
 * Proporciona una estructura de datos de lista enlazada inmutable,
 * Características principales:
 * 
 * - Inmutabilidad: Una vez creada, la lista no puede ser modificada.
 * - Genérica: Puede contener elementos de cualquier tipo.
 * - Eficiente en memoria: Permite compartir estructura entre listas.
 * - Iterable: Se puede recorrer usando bucles for-each y otras construcciones que esperan Iterable.
 * 
 * La clase implementa:
 * 1. Métodos para comparación (equals) y generación de hash code.
 * 2. Un iterador personalizado para recorrer la lista.
 * 3. Soporte para ser utilizada como Iterable.
 * 
 * Uso típico:
 * 
 * Esta implementación es  útil en contextos donde se necesita la inmutabilidad, como en operaciones
 * concurrentes o en algoritmos que se benefician de estructuras de datos inmutables.
 */
public class ImmutableListNode<T> {
    public final T head; //El valor del elemnto actual
    public final ImmutableListNode<T> tail; // referencia al resto de la lista

    public ImmutableListNode(T head, ImmutableListNode<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    // Metodo para comparar si dos listas son iguales
    public boolean equals(Object obj) {
        ImmutableListNode<?> o = (ImmutableListNode<?>)obj;
         // Compara el elemento actual y recursivamente el resto de la lista
        return o != null && head.equals(o.head) &&
            (tail == null ? o.tail == null : tail.equals(o.tail));
    }

    // Genera un código hash para la lista
    public int hashCode() {
        return head.hashCode() + 31*(tail == null ? 1 : tail.hashCode());
    }

    // Clase interna para iterar sobre la lista
    private static class ListIterator<T> implements Iterator<T> {
        private ImmutableListNode<T> list;

        public ListIterator(ImmutableListNode<T> list) {
            this.list = list;
        }

        public boolean hasNext() {
            return list != null;
        }

        public T next() {
            T x = list.head;
            list = list.tail;
            return x;
        }
    }

    // Clase interna para hacer la lista iterable
    private static class ListIterable<T> implements Iterable<T> {
        private final ImmutableListNode<T> list;

        public ListIterable(ImmutableListNode<T> list) {
            this.list = list;
        }

         // Compara si dos iterables son iguales
        public boolean equals(Object obj) {
            ListIterable<?> o = (ListIterable<?>)obj;
            return o != null &&
                (list == null ? o.list == null : list.equals(o.list));
        }

        // Genera un código hash para el iterable
        public int hashCode() {
            return list == null ? 1 : list.hashCode();
        }

        // Crea un nuevo iterador para esta lista
        public Iterator<T> iterator() {
            return new ListIterator<T>(list);
        }
    }

    public static <T> Iterable<T> iterable(final ImmutableListNode<T> list) {
        return new ListIterable<T>(list);
    }
}