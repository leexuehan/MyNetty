package tech.netty.memorymodel;

/**
 * @author leexuehan on 2019/6/24.
 */

/**
 * page的下属分配单元
 */
final class SubPage<T> {
    SubPage<T> prev;
    SubPage<T> next;


    SubPage(int pageSize) {

    }
}
