import com.google.gson.Gson
import spark.Request
import spark.Response
import spark.Spark.*
import java.sql.DriverManager

fun main() {
    val conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test3","sa", "sa")
    try {
        val stmt = conn.createStatement()
        stmt.execute("CREATE TABLE IF NOT EXISTS TBL01(id int primary key auto_increment,a varchar(255) not null,b varchar(255) not null);")
        conn.close()
    } catch (e: Exception) {
        println(e.message)
    }

    port(getHerokuPort())

    staticFiles.location("/public")
    get("/all") { request, response -> getData(request, response) }
    get("/add") {request, response -> addData(request, response) }
    get("/delete") {request, response -> deleteData(request, response) }
    get("/update") {request, response -> updateData(request, response) }
    get("/mass") {request, response ->  massData(request, response) }


}



fun getHerokuPort(): Int {
    val processBuilder = ProcessBuilder()
    return if (processBuilder.environment()["PORT"] != null) {
        processBuilder.environment()["PORT"]!!.toInt()
    } else 5000
}
fun getData(request: Request, response: Response): String? {
    val conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test3", "sa", "sa")

    val stmt = conn.createStatement()
    val xd = stmt.executeQuery("SELECT id, a, b FROM TBL01")
    //
    var Dane = "Dane: ";
    while (xd.next()) {
        var wiersz = " ${xd.getString("id")}:{"+"id:"+"${xd.getString("id")}, " +"a:" + "${xd.getString("a")}, " +"b:"+  "${xd.getString("b")},"+"}, ";
        println(wiersz)
        Dane += "$wiersz ";
    }
    println(xd)
    conn.close()
    return Gson().toJson(Dane)
}

fun addData(request: Request, response: Response): String?{
    val conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test3", "sa", "sa")
    try {
        val stmt = conn.createStatement()
        stmt.execute("INSERT INTO TBL01 (a, b) VALUES('aaa', 'bbb');")
        conn.close()
    } catch (e: Exception) {
        println(e.message)
    }
    var Dane = "Rekord został dodany"
    return Gson().toJson(Dane)
}

fun deleteData(request: Request, response: Response): String?{
    val conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test3", "sa", "sa")
    try {
        val stmt = conn.createStatement()
        stmt.execute("DELETE FROM TBL01;")
        conn.close()
    } catch (e: Exception) {
        println(e.message)
    }
    conn.close()
    var Dane = "Rekordy zostały usunięte"
    return Gson().toJson(Dane)
}

fun updateData(request: Request, response: Response): String?{
    val conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test3", "sa", "sa")
    try {
        val stmt = conn.createStatement()
        stmt.executeUpdate("UPDATE TBL01 SET a='updated a'")
        conn.close()


    } catch (e: Exception) {
        println("xd")

    }
    conn.close()
    var Dane = "Rekordy zostały zupdatowane"
    return Gson().toJson(Dane)
}

fun massData(request: Request, response: Response): String?{
    val conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test3", "sa", "sa")
    val stmt = conn.createStatement()
    val xd = stmt.executeQuery("SELECT id FROM TBL01")
    //
    var Dane = 0;
    while (xd.next()) {
        Dane++
    }
    conn.close()
    return Gson().toJson("Dodano "+Dane+" rekordy")
}