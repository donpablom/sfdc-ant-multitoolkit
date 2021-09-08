println "CompareTargetWithBranch"
String srcDir = "${properties['component.src']}".minus("../../")
new File('artifact/ctemp/deploy_delta-diff.txt').eachLine{
    line ->
        if (line.indexOf("${srcDir}/") > 0) {
            String pathBranch = line.substring(line.indexOf("${srcDir}/"))
            String pathRetr = line.substring(line.indexOf("${srcDir}/") + 4)
            String command = 'diff -q artifact/delta/' + pathRetr + ' artifact/ctarget/test_repo/' + pathBranch

            println "Result: " + pathBranch
            println command
            def outputStream =  command.execute()

            def proc = new StringBuffer()
            outputStream.waitForProcessOutput(proc, System.err)
            println(outputStream.toString())
        }
}
