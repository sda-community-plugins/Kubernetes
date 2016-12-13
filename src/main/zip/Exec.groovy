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
String pod = props['pod']?.trim()
String container = props['container']?.trim()
String command = props['command']?.trim()

KubernetesHelper kh = new KubernetesHelper(new File(".").canonicalFile,
        (path == null) || path.trim().isEmpty() ? "kubectl" : path)

ArrayList args = []
kh.setGlobals(args, url, username, password, namespace, globals)
args << 'exec'
args << pod
if (!kh.isEmpty(container)) args << '-c ' + container
args << command
kh.setFlags(args, flags)

if (kh.runCommand('Executing command...', args)) {
    System.exit(0)
} else {
    System.exit(1)
}
