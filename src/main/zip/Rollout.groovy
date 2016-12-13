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
String command = props['command']?.trim()
String type = props['type']?.trim()
String resource = props['resource']?.trim()

KubernetesHelper kh = new KubernetesHelper(new File(".").canonicalFile,
        (path == null) || path.trim().isEmpty() ? "kubectl" : path)

ArrayList args = []
kh.setGlobals(args, url, username, password, namespace, globals)
args << 'rollout'
args << command
if (type.equals('filename')) {
    args << '-f'
} else if (type.equals('deployment')) {
    args << 'deployment'
} else if (type.equals('rc')) {
    args << 'rc'
} else if (type.equals('service')) {
    args << 'service'
} else if (type.equals('pod')) {
    args << 'pod'
}
args << resource
kh.setFlags(args, flags)

if (kh.runCommand('Executing rollout on resource(s)...', args)) {
    System.exit(0)
} else {
    System.exit(1)
}
