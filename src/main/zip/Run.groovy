import com.serena.air.plugin.kubernetes.KubernetesHelper
import com.urbancode.air.AirPluginTool

def apTool = new AirPluginTool(this.args[0], this.args[1])
def props = apTool.getStepProperties()

String url = props['url']?.trim()
String username = props['username']?.trim()
String password = props['password']?.trim()
String namespace = props['namespace']?.trim()
String path = props['path']?.trim()
String globals = props['globals']?.trim()
String flags = props['flags']?.trim()
String name = props['name']?.trim()
String image = props['image']?.trim()
def envPropValues = props["envPropValues"]?.trim()


KubernetesHelper kh = new KubernetesHelper(new File(".").canonicalFile,
        (path == null) || path.trim().isEmpty() ? "kubectl" : path)

ArrayList args = []
kh.setGlobals(args, url, username, password, namespace, globals)
args << 'run'
args << name
args << '--image=' + image
if (envPropValues) {
    envPropValues.split("(?<=(^|[^\\\\])(\\\\{2}){0,8}),").each { prop ->
        //split out the name
        def parts = prop.split("(?<=(^|[^\\\\])(\\\\{2}){0,8})=", 2)
        def propName = parts[0]
        def propValue = parts.size() == 2 ? parts[1] : ""
        //replace \, with just , and then \\ with \
        propName = propName.replace("\\=", "=").replace("\\,", ",").replace("\\\\", "\\")
        propValue = propValue.replace("\\=", "=").replace("\\,", ",").replace("\\\\", "\\")
        args << '--env "' + propName + '=' + propValue + '"'
        kh.debug("setting environment variable: '${propName}=${propValue}'")
    }
}
kh.setFlags(args, flags)

if (kh.runCommand('Running image(s)...', args)) {
    System.exit(0)
} else {
    System.exit(1)
}

