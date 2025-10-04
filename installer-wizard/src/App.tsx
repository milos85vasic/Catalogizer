import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import WizardLayout from './components/layout/WizardLayout'
import WelcomeStep from './components/wizard/WelcomeStep'
import NetworkScanStep from './components/wizard/NetworkScanStep'
import SMBConfigurationStep from './components/wizard/SMBConfigurationStep'
import ConfigurationManagementStep from './components/wizard/ConfigurationManagementStep'
import SummaryStep from './components/wizard/SummaryStep'
import { WizardProvider } from './contexts/WizardContext'
import { ConfigurationProvider } from './contexts/ConfigurationContext'

function App() {
  return (
    <ConfigurationProvider>
      <WizardProvider>
        <Router>
          <div className="min-h-screen bg-background">
            <Routes>
              <Route path="/" element={<WizardLayout />}>
                <Route index element={<WelcomeStep />} />
                <Route path="scan" element={<NetworkScanStep />} />
                <Route path="configure" element={<SMBConfigurationStep />} />
                <Route path="manage" element={<ConfigurationManagementStep />} />
                <Route path="summary" element={<SummaryStep />} />
              </Route>
            </Routes>
          </div>
        </Router>
      </WizardProvider>
    </ConfigurationProvider>
  )
}

export default App