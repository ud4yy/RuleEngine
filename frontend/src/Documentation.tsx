import React from 'react';

const Documentation: React.FC = () => {
  return (
    <div>
      {/* Note for users about potential delays */}
      <div className="bg-yellow-100 p-4 rounded mb-6">
        <h2 className="text-lg font-semibold text-yellow-800">Important Note</h2>
        <p className="text-yellow-700">
          The server runs on a free instance, which may spin down after periods of inactivity. 
          As a result, if this is your first request after a while, there could be a delay of 50 seconds or more 
          while the server wakes up. Please be patient during this time.
        </p>
      </div>

      {/* How to Use the Rule Engine */}
      <h2 className="text-xl font-semibold text-teal-600 mb-4">How to Use the Rule Engine</h2>
      <div className="space-y-2">
        <div>
          <h3 className="font-medium text-gray-800">Create a Rule</h3>
          <p>Enter your rule in the text area and click "Create Rule".</p>
          <p className="text-sm text-gray-600">Example rule:</p>
          <pre className="bg-gray-100 p-2 rounded font-mono text-sm">
            {`rule2 = ((age > 30 AND department = 'Marketing') 
          AND (salary > 20000 OR experience > 5))`}
          </pre>
        </div>
        <div>
          <h3 className="font-medium text-gray-800">Evaluate a Rule</h3>
          <p>Select an existing rule from the dropdown, provide JSON input.</p>
          <p className="text-sm text-gray-600">Example JSON test-data:</p>
          <pre className="bg-gray-100 p-2 rounded font-mono text-sm">
            {`{
  "age": 35,
  "department": "Sales",
  "salary": 60000,
  "experience": 3
}`}
          </pre>
        </div>
        <div>
          <h3 className="font-medium text-gray-800">Combine Rules</h3>
          <p>Select multiple rules and click "Combine Rules" to create a new rule string.</p>
        </div>
        <div>
          <h3 className="font-medium text-gray-800">Results</h3>
          <p>The evaluation result will be displayed below the input fields.</p>
        </div>
      </div>
      <p className="mt-4 text-sm text-gray-600">
        <a href=''>For more details, check the full documentation.  </a>
      </p>
    </div>
  );
};

export default Documentation;
