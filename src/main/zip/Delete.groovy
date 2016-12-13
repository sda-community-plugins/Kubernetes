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
String type = props['type']?.trim()
String resources = props['resources']?.trim()

KubernetesHelper kh = new KubernetesHelper(new File(".").canonicalFile,
        (path == null) || path.trim().isEmpty() ? "kubectl" : path)

ArrayList args = []
kh.setGlobals(args, url, username, password, namespace, globals)
args << 'delete'
if (type.equals('filename')) {
    args << '-f'
    resources.split('\\s+').each() { resource ->
        if (resource) {
            args << resource
        }
    }
} else if (type.equals('names')) {
    resources.split('\\s+').each() { resource ->
        if (resource) {
            args << resource
        }
    }
} else if (type.equals('labels')) {
    resources = resources.split('\\s+')
    args << resources[0]
    args << '-l'
    args << resources[1]
} else if (type.equals('all')) {
    resources.split('\\s+').each() { resource ->
        if (resource) {
            args << resource
        }
    }
    args << '--all'
}
kh.setFlags(args, flags)

if (kh.runCommand('Deleting resource(s)...', args)) {
    System.exit(0)
} else {
    System.exit(1)
}
