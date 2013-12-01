package models

import com.jolbox.bonecp.BoneCP
import com.jolbox.bonecp.BoneCPConfig
import net.fwbrasil.activate.ActivateContext
import net.fwbrasil.activate.storage.relational.JdbcRelationalStorage
import net.fwbrasil.activate.storage.relational.idiom.h2Dialect
import play.api.Play 
import net.fwbrasil.activate.storage.relational.idiom.SqlIdiom

trait PooledLoggingJdbcRelationalStorage extends JdbcRelationalStorage with DelayedInit {

    val jdbcDriver: String
    val url: String
    val user: String
    val password: String
    val logStatements: Boolean

    private var _connectionPool: BoneCP = _

    override def delayedInit(body: => Unit) = {
        body
        initConnectionPool
    }

    def connectionPool =
        _connectionPool

    override def getConnection =
        _connectionPool.getConnection

    private def initConnectionPool = {
        Class.forName(jdbcDriver)
        val config = new BoneCPConfig
        config.setJdbcUrl(url)
        config.setUsername(user)
        config.setPassword(password)
        config.setLazyInit(true)
        config.setDisableConnectionTracking(true)
        config.setReleaseHelperThreads(0)
        config.setLogStatementsEnabled(logStatements)
        _connectionPool = new BoneCP(config)
    }

}

object tasksContext extends ActivateContext {
    val storage = new PooledLoggingJdbcRelationalStorage {
    	println(Play.current.configuration.getString("activate.storage.tasksContext.jdbcDriver"))
        val jdbcDriver = Play.current.configuration.getString("activate.storage.tasksContext.jdbcDriver").get
        val user = Play.current.configuration.getString("activate.storage.tasksContext.user").get
        val password = Play.current.configuration.getString("activate.storage.tasksContext.password").get
        val url = Play.current.configuration.getString("activate.storage.tasksContext.url").get
        val dialect = SqlIdiom.dialect(Play.current.configuration.getString("activate.storage.tasksContext.dialect").get)
        val logStatements = Play.current.configuration.getBoolean("activate.storage.tasksContext.logStatements").get
    }
}