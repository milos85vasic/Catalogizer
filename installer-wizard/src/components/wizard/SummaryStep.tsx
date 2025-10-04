import { useEffect } from 'react'
import { Button } from '../ui/Button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/Card'
import { useWizard } from '../../contexts/WizardContext'
import { useConfiguration } from '../../contexts/ConfigurationContext'
import { TauriService } from '../../services/tauri'
import {
  CheckCircle,
  FileText,
  Download,
  Upload,
  Network,
  Settings,
  AlertTriangle,
  ExternalLink,
  RefreshCw
} from 'lucide-react'

export default function SummaryStep() {
  const { setCanNext } = useWizard()
  const { state: configState } = useConfiguration()

  useEffect(() => {
    // Summary step can always proceed (it's the final step)
    setCanNext(false) // Actually, this is the final step, so no "Next" button
  }, [setCanNext])

  const handleStartOver = () => {
    window.location.reload()
  }

  const handleSaveAgain = async () => {
    if (configState.configuration) {
      try {
        await TauriService.saveConfigurationFile(configState.configuration)
      } catch (error) {
        console.error('Failed to save configuration:', error)
      }
    }
  }

  const getConfigurationSummary = () => {
    const { configuration } = configState

    return {
      accessCount: configuration.accesses.length,
      sourceCount: configuration.sources.length,
      smbSources: configuration.sources.filter(s => s.type === 'samba').length,
      uniqueHosts: new Set(
        configuration.sources
          .map(s => s.url.match(/smb:\/\/([^:/]+)/)?.[1])
          .filter(Boolean)
      ).size,
    }
  }

  const summary = getConfigurationSummary()

  return (
    <div className="space-y-6">
      <div className="text-center space-y-4">
        <div className="mx-auto w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
          <CheckCircle className="h-8 w-8 text-green-600" />
        </div>
        <h2 className="text-xl font-bold text-gray-900">Setup Complete!</h2>
        <p className="text-gray-600">
          Your Catalogizer installation wizard has completed successfully
        </p>
      </div>

      {/* Configuration Summary */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FileText className="h-5 w-5" />
            Configuration Summary
          </CardTitle>
          <CardDescription>
            Overview of your configured SMB sources
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="text-center p-4 bg-blue-50 rounded-lg">
              <Upload className="h-8 w-8 text-blue-600 mx-auto mb-2" />
              <div className="text-2xl font-bold text-blue-900">{summary.accessCount}</div>
              <div className="text-sm text-blue-700">Access Credentials</div>
            </div>

            <div className="text-center p-4 bg-green-50 rounded-lg">
              <Download className="h-8 w-8 text-green-600 mx-auto mb-2" />
              <div className="text-2xl font-bold text-green-900">{summary.sourceCount}</div>
              <div className="text-sm text-green-700">Media Sources</div>
            </div>

            <div className="text-center p-4 bg-purple-50 rounded-lg">
              <Network className="h-8 w-8 text-purple-600 mx-auto mb-2" />
              <div className="text-2xl font-bold text-purple-900">{summary.smbSources}</div>
              <div className="text-sm text-purple-700">SMB Sources</div>
            </div>

            <div className="text-center p-4 bg-orange-50 rounded-lg">
              <Settings className="h-8 w-8 text-orange-600 mx-auto mb-2" />
              <div className="text-2xl font-bold text-orange-900">{summary.uniqueHosts}</div>
              <div className="text-sm text-orange-700">Unique Hosts</div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Configured Sources */}
      <Card>
        <CardHeader>
          <CardTitle>Configured Sources</CardTitle>
          <CardDescription>
            List of all configured SMB sources
          </CardDescription>
        </CardHeader>
        <CardContent>
          {configState.configuration.sources.length > 0 ? (
            <div className="space-y-3">
              {configState.configuration.sources.map((source, index) => (
                <div key={index} className="p-4 border rounded-lg">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                      <Network className="h-5 w-5 text-blue-600" />
                    </div>
                    <div className="flex-1">
                      <div className="font-medium">{source.type.toUpperCase()}</div>
                      <div className="text-sm text-gray-500 break-all">{source.url}</div>
                      <div className="text-xs text-gray-400">Access: {source.access}</div>
                    </div>
                    <div className="text-sm text-green-600 font-medium">
                      ✓ Configured
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8 text-gray-500">
              <AlertTriangle className="h-12 w-12 mx-auto mb-4 text-yellow-500" />
              <p className="text-lg font-medium">No sources configured</p>
              <p className="text-sm">
                Consider going back to add some SMB sources
              </p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Next Steps */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <ExternalLink className="h-5 w-5" />
            Next Steps
          </CardTitle>
          <CardDescription>
            How to use your configuration with Catalogizer
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="flex items-start gap-3">
              <div className="w-6 h-6 bg-blue-600 text-white rounded-full flex items-center justify-center text-sm font-medium">
                1
              </div>
              <div>
                <div className="font-medium">Deploy your configuration</div>
                <div className="text-sm text-gray-600">
                  Copy the saved configuration file to your Catalogizer server installation directory
                </div>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <div className="w-6 h-6 bg-blue-600 text-white rounded-full flex items-center justify-center text-sm font-medium">
                2
              </div>
              <div>
                <div className="font-medium">Start Catalogizer server</div>
                <div className="text-sm text-gray-600">
                  Launch the Catalogizer server with your new configuration
                </div>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <div className="w-6 h-6 bg-blue-600 text-white rounded-full flex items-center justify-center text-sm font-medium">
                3
              </div>
              <div>
                <div className="font-medium">Access the web interface</div>
                <div className="text-sm text-gray-600">
                  Open the Catalogizer web interface to manage your media collection
                </div>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <div className="w-6 h-6 bg-blue-600 text-white rounded-full flex items-center justify-center text-sm font-medium">
                4
              </div>
              <div>
                <div className="font-medium">Monitor and enjoy</div>
                <div className="text-sm text-gray-600">
                  Watch as Catalogizer automatically discovers and catalogs your media files
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Important Notes */}
      <Card className="border-yellow-200 bg-yellow-50">
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-yellow-800">
            <AlertTriangle className="h-5 w-5" />
            Important Notes
          </CardTitle>
        </CardHeader>
        <CardContent>
          <ul className="space-y-2 text-yellow-800 text-sm">
            <li>• Ensure your SMB credentials are secure and follow your organization's security policies</li>
            <li>• Test your configuration in a development environment before deploying to production</li>
            <li>• Keep your configuration file backed up and version controlled</li>
            <li>• Monitor SMB connection logs for any authentication or connectivity issues</li>
            <li>• Update credentials in the configuration file if SMB passwords change</li>
          </ul>
        </CardContent>
      </Card>

      {/* Action Buttons */}
      <div className="flex flex-col sm:flex-row gap-4 justify-center">
        <Button
          variant="outline"
          onClick={handleStartOver}
          className="flex items-center gap-2"
        >
          <RefreshCw className="h-4 w-4" />
          Start Over
        </Button>

        <Button
          onClick={handleSaveAgain}
          className="flex items-center gap-2"
        >
          <Download className="h-4 w-4" />
          Save Configuration Again
        </Button>
      </div>

      {/* Final Success Message */}
      <div className="text-center p-6 bg-green-50 border border-green-200 rounded-lg">
        <CheckCircle className="h-12 w-12 text-green-600 mx-auto mb-4" />
        <h3 className="text-lg font-semibold text-green-900 mb-2">
          Catalogizer Installation Wizard Complete!
        </h3>
        <p className="text-green-800">
          Your SMB sources have been configured successfully. You can now use the generated
          configuration file with your Catalogizer installation.
        </p>
      </div>
    </div>
  )
}