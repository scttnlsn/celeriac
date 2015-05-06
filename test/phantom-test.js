var page = require('webpage').create();

page.onConsoleMessage = function (message) {
  console.log(message);
};

page.onCallback = function (data) {
  if (data.failures === 0) {
    console.log('Tests passed.');
    phantom.exit(0);
  } else {
    console.log('Tests failed.');
    phantom.exit(1);
  }
};

page.open('test/test.html', function (status) {
  if (status !== 'success') {
    console.log('Failed to open ' + url);
    phantom.exit(1);
  }

  page.evaluate(function () {
    window.celeriac.test_runner.register_callback(function (failures) {
      window.callPhantom({ failures: failures });
    });

    window.celeriac.test_runner.run();
  });
});
