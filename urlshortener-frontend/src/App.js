import React, { useState } from 'react';

function App() {
  const [longUrl, setLongUrl] = useState('');
  const [shortUrl, setShortUrl] = useState('');
  const [error, setError] = useState('');

  // Handle input change for long URL
  const handleLongUrlChange = (event) => {
    setLongUrl(event.target.value);
  };

  // Handle URL shortening
  const shortenUrl = async () => {
    try {
      const response = await fetch('http://localhost:8081/urlshortner/shorten', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ longUrl }),
      });

      if (response.ok) {
        const data = await response.text(); // The short URL will be in the response body
        setShortUrl(data); // Update state with short URL
        setError(''); // Clear any previous errors
      } else {
        throw new Error('Failed to shorten URL');
      }
    } catch (err) {
      setError(err.message); // Display error if something goes wrong
    }
  };

  const handleFetchLongUrl = async () => {
    try {
      const response = await fetch(`http://localhost:8081/urlshortner/${shortUrl}`);

      if (response.ok) {
        // This will automatically redirect to the long URL
        window.location.href = response.url;
      } else {
        console.error('Error fetching long URL');
      }
    } catch (error) {
      console.error('Error occurred while fetching long URL:', error);
    }
  };


  return (
    <div className="App">
      <h1>URL Shortener</h1>

      <div>
        <label>Enter Long URL:</label>
        <input
          type="text"
          value={longUrl}
          onChange={handleLongUrlChange}
          placeholder="Enter long URL"
        />
      </div>

      <button onClick={shortenUrl}>Shorten URL</button>

      {shortUrl && (
        <div>
          <h2>Shortened URL:</h2>
          <p>{shortUrl}</p>
        </div>
      )}

      {error && <p style={{ color: 'red' }}>{error}</p>}
    </div>
  );
}

export default App;
