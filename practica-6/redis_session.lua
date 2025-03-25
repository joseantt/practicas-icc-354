local redis = require "redis"

local redis_host = "redis"
local redis_port = 6379
local redis_timeout = 1000

local servers = {
    "app1",
    "app2",
    "app3"
}

-- Connect to Redis
local function connect_redis()
    local client = redis.connect(redis_host, redis_port)
    client:set_timeout(redis_timeout)
    return client
end

function redis_session_persistence(txn)
    local session_id = txn:get_var("txn.sessionid")

    if not session_id or session_id == "" then
        return
    end

    local client = connect_redis()
    if not client then
        txn:Warning("Failed to connect to Redis")
        return
    end

    local server = client:get("session:" .. session_id)

    if server then
        txn:set_var("req.backend_server", server)
    else
        local chosen_server = servers[math.random(#servers)]
        client:setex("session:" .. session_id, 1800, chosen_server)
        txn:set_var("req.backend_server", chosen_server)
    end

    client:close()
end

core.register_action("redis_session_persistence", {"http-req"}, redis_session_persistence)

