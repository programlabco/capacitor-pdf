
  Pod::Spec.new do |s|
    s.name = 'CapacitorPdf'
    s.version = '0.0.1'
    s.summary = 'Pdf plugin for capacitor apps'
    s.license = 'MIT'
    s.homepage = 'https://github.com/programlabco/capacitor-pdf'
    s.author = 'ProgramLab'
    s.source = { :git => 'https://github.com/programlabco/capacitor-pdf', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp,storyboard}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end
