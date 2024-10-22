import React, { useState, useEffect } from 'react';

interface Rule {
  id: string;
  jsonObject: any;
  rule: string | null;
}

const RuleEngine: React.FC = () => {
  const [ruleString, setRuleString] = useState('');
  const [jsonInput, setJsonInput] = useState('');
  const [existingRules, setExistingRules] = useState<Rule[]>([]);
  const [result, setResult] = useState<boolean | null>(null);
  const [selectedRules, setSelectedRules] = useState<string[]>([]);
  const [selectedRuleForEvaluation, setSelectedRuleForEvaluation] = useState<string>('');
  const [message, setMessage] = useState<{ type: 'error' | 'success', text: string } | null>(null);
  const [isEditing, setIsEditing] = useState<string | null>(null);
  const [viewFullRuleId, setViewFullRuleId] = useState<string | null>(null);

  useEffect(() => {
    fetchAllRules();
  }, []);

  const fetchAllRules = async () => {
    try {
      const response = await fetch('https://ruleengine-9406.onrender.com/api/trees/getall');
      if (response.ok) {
        const rules: Rule[] = await response.json();
        setExistingRules(rules);
      } else {
        setMessage({ type: 'error', text: 'Failed to fetch rules' });
      }
    } catch (error) {
      setMessage({ type: 'error', text: 'Error fetching rules' });
    }
  };

  const handleCreateRule = async () => {
    if (ruleString) {
      try {
        const response = await fetch('https://ruleengine-9406.onrender.com/api/trees/create', {
          method: 'POST',
          headers: {
            'Content-Type': 'text/plain',
          },
          body: ruleString,
        });
        console.log(response);
        if (response.ok) {
          const newRule: Rule = await response.json();
          setExistingRules([...existingRules, newRule]);
          setRuleString('');
          console.log(response.ok);
          setMessage({ type: 'success', text: 'Rule created successfully' });
        } else {
          setMessage({ type: 'error', text: 'Failed to create rule' });
        }
      } catch (error) {
        setMessage({ type: 'error', text: 'Error creating rule' });
      }
    }
  };

  const handleModifyRule = async (ruleId: string, newRule: string) => {
    try {
      const response = await fetch(`https://ruleengine-9406.onrender.com/api/trees/modify/${ruleId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'text/plain',
        },
        body: newRule,
      });
      if (response.ok) {
        const updatedRule: Rule = await response.json();
        setExistingRules(existingRules.map((rule) => (rule.id === ruleId ? updatedRule : rule)));
        setIsEditing(null);
        setMessage({ type: 'success', text: 'Rule modified successfully' });
      } else {
        setMessage({ type: 'error', text: 'Failed to modify rule' });
      }
    } catch (error) {
      setMessage({ type: 'error', text: 'Error modifying rule' });
    }
  };

  const handleDeleteRule = async (ruleId: string) => {
    try {
      const response = await fetch(`https://ruleengine-9406.onrender.com/api/trees/delete/${ruleId}`, {
        method: 'DELETE',
      });
      if (response.ok) {
        setExistingRules(existingRules.filter((rule) => rule.id !== ruleId));
        setMessage({ type: 'success', text: 'Rule deleted successfully' });
      } else {
        setMessage({ type: 'error', text: 'Failed to delete rule' });
      }
    } catch (error) {
      setMessage({ type: 'error', text: 'Error deleting rule' });
    }
  };

  const handleEvaluateRule = async () => {
    if (selectedRuleForEvaluation && jsonInput) {
      try {
        const response = await fetch(`https://ruleengine-9406.onrender.com/api/trees/evaluate/${selectedRuleForEvaluation}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: jsonInput,
        });
        if (response.ok) {
          const result = await response.json();
          setResult(result);
          setMessage({ type: 'success', text: 'Rule evaluated successfully' });
        } else {
          setMessage({ type: 'error', text: 'Failed to evaluate rule' });
        }
      } catch (error) {
        setMessage({ type: 'error', text: 'Error evaluating rule' });
      }
    }
  };

  const handleCombineRules = async () => {
    if (selectedRules.length > 1) {
      try {
        const response = await fetch('https://ruleengine-9406.onrender.com/api/trees/combine', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(selectedRules),
        });
        if (response.ok) {
          const combinedRule: Rule = await response.json();
          setExistingRules([...existingRules, combinedRule]);
          setSelectedRules([]);
          setMessage({ type: 'success', text: 'Rules combined successfully' });
        } else {
          setMessage({ type: 'error', text: 'Failed to combine rules' });
        }
      } catch (error) {
        setMessage({ type: 'error', text: 'Error combining rules' });
      }
    } else {
      setMessage({ type: 'error', text: 'Please select at least two rules to combine' });
    }
  };

  
  const handleRuleSelection = (ruleId: string) => {
    setSelectedRules(prev => 
      prev.includes(ruleId) 
        ? prev.filter(id => id !== ruleId)
        : [...prev, ruleId]
    );
  };

  return (
    <div className="p-6 max-w-6xl mx-auto">
      <h1 className="text-3xl font-bold text-teal-600 mb-6">Rule Engine</h1>
      
      {message && (
        <div className={`p-4 mb-4 rounded ${message.type === 'error' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`}>
          {message.text}
        </div>
      )}
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Create and Evaluate Section */}
        <div className="bg-gray-100 rounded-lg shadow p-6">
          <h2 className="text-xl font-semibold text-teal-600 mb-4">Create Rule</h2>
          <textarea
            className="w-full p-2 border border-gray-300 rounded mb-4"
            value={ruleString}
            onChange={(e) => setRuleString(e.target.value)}
            placeholder="Enter Rule String"
          />
          <button
            className="bg-teal-600 text-white rounded px-4 py-2 hover:bg-teal-700 transition"
            onClick={handleCreateRule}
          >
            Create Rule
          </button>

          <h2 className="text-xl font-semibold text-teal-600 mt-6 mb-4">Evaluate Rule</h2>
          <select 
            className="w-full border border-gray-300 rounded p-2 mb-4"
            value={selectedRuleForEvaluation}
            onChange={(e) => setSelectedRuleForEvaluation(e.target.value)}
          >
            <option value="">Select a rule</option>
            {existingRules.map((rule) => (
              <option key={rule.id} value={rule.id}>
                {rule.rule || JSON.stringify(rule.jsonObject)}
              </option>
            ))}
          </select>
          <textarea
            className="w-full p-2 border border-gray-300 rounded mb-4"
            value={jsonInput}
            onChange={(e) => setJsonInput(e.target.value)}
            placeholder='{"age": 35, "salary": 60000, "experience": 3}'
          />
          <button
            className="bg-teal-600 text-white rounded px-4 py-2 hover:bg-teal-700 transition"
            onClick={handleEvaluateRule}
          >
            Evaluate
          </button>

          <h2 className="text-xl font-semibold text-teal-600 mt-6 mb-4">Evaluation Result</h2>
          <div className="font-bold bg-gray-200 p-4 rounded">
            {result === null ? 'N/A' : result ? 'True' : 'False'}
          </div>
        </div>

        {/* Existing Rules Section */}
        <div className="bg-gray-100 rounded-lg shadow p-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold text-teal-600">Existing Rules</h2>
            {selectedRules.length >= 0 && (
              <button 
                className="bg-teal-600 text-white rounded px-4 py-2 hover:bg-teal-700 transition"
                onClick={handleCombineRules}
              >
                Combine Selected Rules
              </button>
            )}
          </div>
          <div className="h-96 overflow-y-auto border border-gray-300 rounded-lg mb-4 p-2">
            <ul className="list-none">
              {existingRules.map((rule) => (
                <li key={rule.id} className="py-2 border-b border-gray-300 last:border-b-0">
                  <div className="flex flex-col space-y-2">
                    <div className="flex items-start justify-between">
                      <div className="flex items-start space-x-3 flex-grow pr-4">
                        <input
                          type="checkbox"
                          checked={selectedRules.includes(rule.id)}
                          onChange={() => handleRuleSelection(rule.id)}
                          className="mt-1"
                        />
                        <span className="block">
                          {viewFullRuleId === rule.id ? rule.rule : rule.rule?.slice(0, 50)}
                          {rule.rule && rule.rule.length > 50 && (
                            <button
                              className="text-teal-600 hover:text-teal-700 underline ml-2"
                              onClick={() => setViewFullRuleId(viewFullRuleId === rule.id ? null : rule.id)}
                            >
                              {viewFullRuleId === rule.id ? 'Hide' : 'View More'}
                            </button>
                          )}
                        </span>
                      </div>
                      <div className="flex items-center space-x-2 flex-shrink-0">
                        <button
                          className="bg-blue-500 hover:bg-blue-600 text-white rounded px-3 py-1 text-sm transition-colors"
                          onClick={() => setIsEditing(rule.id)}
                        >
                          Edit
                        </button>
                        <button
                          className="bg-red-500 hover:bg-red-600 text-white rounded px-3 py-1 text-sm transition-colors"
                          onClick={() => handleDeleteRule(rule.id)}
                        >
                          Delete
                        </button>
                      </div>
                    </div>
                    {isEditing === rule.id && (
                      <div className="mt-2 w-full">
                        <textarea
                          className="w-full p-2 border border-gray-300 rounded mb-2"
                          defaultValue={rule.rule ?? ''} 
                          onChange={(e) => setRuleString(e.target.value)}
                        />
                        <button
                          className="bg-green-500 hover:bg-green-600 text-white rounded px-4 py-2 text-sm transition-colors"
                          onClick={() => handleModifyRule(rule.id, ruleString)}
                        >
                          Save
                        </button>
                      </div>
                    )}
                  </div>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RuleEngine;