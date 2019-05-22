package net.swamphut.swampium.service.spec.db

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.sql.PreparedStatement
import java.sql.ResultSet

interface JdbcService {
    interface ServicePreparedStatement {
        fun config(configFunc: PreparedStatement.() -> Unit): ServicePreparedStatement
        fun execute(): Single<Boolean>
        fun <T> executeQuery(onNext: ResultSet.() -> T): Observable<T>
        fun executeQueryToResultSet(): Single<ResultSet>
        fun executeBatch(): Single<IntArray>
        val connection: ServiceConnection
    }

    interface ServiceConnection {
        fun prepareStatement(statement: String): ServicePreparedStatement
        fun commit(): Completable
        fun close(): Completable
        val isClosed: Boolean
    }

    fun getConnection(dbUrl: String, dbUsername: String, dbPassword: String, autoCommit: Boolean = true): Single<out ServiceConnection>
}