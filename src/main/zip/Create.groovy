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
String filename = props['filename']?.trim()
Boolean saveConfig = Boolean.valueOf(props['saveConfig']?.trim())
Boolean record = Boolean.valueOf(props['record']?.trim())

KubernetesHelper kh = new KubernetesHelper(new File(".").canonicalFile,
        (path == null) || path.trim().isEmpty() ? "kubectl" : path)

ArrayList args = []
kh.setGlobals(args, url, username, password, namespace, globals)
args << 'create'
if (saveConfig) {
    args << '--save-config=true'
}
if (record) {
    args << '--record=true'
}
args << '--filename=' + filename
kh.setFlags(args, flags)

if (kh.runCommand('Creating resource(s)...', args)) {
    System.exit(0)
} else {
    System.exit(1)
}
