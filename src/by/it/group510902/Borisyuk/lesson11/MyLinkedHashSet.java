package by.it.group510902.Borisyuk.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {

    // Узел, который одновременно является элементом хэш-цепочки и элементом двунаправленного списка порядка
    private static class Node<E> {
        E data;
        Node<E> next; // Следующий в хэш-цепочке коллизий

        Node<E> before; // Предыдущий по порядку добавления
        Node<E> after;  // Следующий по порядку добавления

        Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<E>[] table;
    private int size = 0;

    // Указатели на начало и конец списка порядка добавления
    private Node<E> head = null;
    private Node<E> tail = null;

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        table = (Node<E>[]) new Node[16];
    }

    private int getIndex(Object o) {
        if (o == null) {
            return 0;
        }
        return Math.abs(o.hashCode()) % table.length;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;
        table = (Node<E>[]) new Node[oldTable.length * 2];

        // Перестраиваем хэш-таблицу, сохраняя старые узлы и их связи по порядку
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }

        Node<E> current = head;
        while (current != null) {
            int index = getIndex(current.data);
            current.next = table[index];
            table[index] = current;
            current = current.after;
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public boolean add(E e) {
        if (contains(e)) {
            return false;
        }
        if (size >= table.length * 0.75) {
            resize();
        }
        int index = getIndex(e);

        // Создаем узел и добавляем в хэш-таблицу
        Node<E> newNode = new Node<>(e, table[index]);
        table[index] = newNode;

        // Связываем узел в конец двунаправленного списка порядка
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.after = newNode;
            newNode.before = tail;
            tail = newNode;
        }

        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = getIndex(o);
        Node<E> current = table[index];
        Node<E> prev = null;

        while (current != null) {
            if ((o == null && current.data == null) || (o != null && o.equals(current.data))) {
                // 1. Удаляем из хэш-таблицы
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }

                // 2. Исключаем из двунаправленного списка порядка добавления
                if (current.before != null) {
                    current.before.after = current.after;
                } else {
                    head = current.after; // Если это был первый элемент
                }

                if (current.after != null) {
                    current.after.before = current.before;
                } else {
                    tail = current.before; // Если это был последний элемент
                }

                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {
        int index = getIndex(o);
        Node<E> current = table[index];
        while (current != null) {
            if ((o == null && current.data == null) || (o != null && o.equals(current.data))) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> current = head; // Обходим строго по порядку добавления элементов
        while (current != null) {
            sb.append(current.data);
            current = current.after;
            if (current != null) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            if (remove(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Node<E> current = head;
        while (current != null) {
            Node<E> next = current.after; // Сохраняем ссылку на следующий, так как текущий может быть удален
            if (!c.contains(current.data)) {
                remove(current.data);
                modified = true;
            }
            current = next;
        }
        return modified;
    }

    /////////////////////////////////////////////////////////////////////////
    //////        Остальные методы интерфейса Set (заглушки)          ///////
    /////////////////////////////////////////////////////////////////////////

    @Override public Iterator<E> iterator() { return null; }
    @Override public Object[] toArray() { return new Object[0]; }
    @Override public <T> T[] toArray(T[] a) { return null; }
}
