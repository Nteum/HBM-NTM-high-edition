function results = simulate_server_30p(varargin)
%SIMULATE_SERVER_30P
% Stochastic tick/MSPT simulation for a Forge server at 30 players.
% Compares "single mod (HBM only)" vs a heavier "modpack" profile.
%
% Notes:
% - This is a simplified model; calibrate parameters with real profiling if needed.
% - Dedicated servers are CPU+RAM bound; the GPU is usually irrelevant.
% - Minecraft server tick is effectively single-core; multi-core helps only periphery tasks.
%
% Usage:
%   results = simulate_server_30p();
%   results = simulate_server_30p('Players', 30, 'DurationMin', 60, 'Runs', 20);
%
% Parameters (name-value):
%   Players          (default 30)
%   DurationMin      (default 60)
%   Runs             (default 20)  Monte Carlo runs per scenario
%   ViewDistance     (default 10)  chunks
%   XmxSingleGB      (default 10)
%   XmxPackGB        (default 16)
%   Seed             (default 42)
%   CpuSingleRel     (default 0.95) relative to i5-9400F single-thread
%   CpuCores         (default 1)
%   CpuName          (default "AMD EPYC (generic)")
%   SystemRamGB      (default 32)
%   DiskGB           (default 256)
%   DiskType         (default "SSD")
%   ModVersion       (default read from gradle.properties if available)
%
% Output:
%   results.single / results.pack contain:
%     .runs      struct array of run metrics
%     .summary   aggregate metrics across runs
%     .example   representative trace (median avgTPS)
%

cfg = parseInputs(varargin{:});
cfg.ModVersion = resolveModVersion(cfg.ModVersion);
hw = defaultHardware(cfg);

common = struct();
common.players = cfg.Players;
common.durationSec = cfg.DurationMin * 60;
common.tickRate = 20;
common.tickTargetMs = 1000 / common.tickRate;
common.viewDistance = cfg.ViewDistance;
common.gcTriggerFrac = 0.82;
common.gcPostFrac = 0.45;
common.modVersion = cfg.ModVersion;
common.ramBaseGB = 2.5;
common.ramPerPlayerGB = 0.05;
common.diskBaseMBps = 0.2;
common.diskWorldgenMBpsPerMs = 0.35;
common.diskSaveMBpsPerMs = 0.60;
common.diskRareMBpsPerMs = 0.20;
common.netBaseMbps = 0.2;
common.netPerPlayerMbps = 0.04;
common.netWorldgenMbpsPerMs = 0.10;
common.netSaveMbpsPerMs = 0.05;

singleScenario = scenarioSingleModHBM(cfg);
packScenario = scenarioModpack(cfg);

single = runMonteCarlo(common, singleScenario, hw, cfg.Runs, cfg.Seed + 1000);
pack = runMonteCarlo(common, packScenario, hw, cfg.Runs, cfg.Seed + 2000);

printComparison(single.summary, pack.summary, hw, common);
animateResources(single.example, pack.example, hw, common);
plotExample(single.example, pack.example, hw, common);
plotMonteCarlo(single.runs, pack.runs);

results = struct();
results.hardware = hw;
results.common = common;
results.single = single;
results.pack = pack;

end

function cfg = parseInputs(varargin)
parser = inputParser;
parser.FunctionName = 'HBM-V1.3.1b1024+航天版服务器基线测试';

addParameter(parser, 'Players', 30, @(x) isnumeric(x) && isscalar(x) && x > 0);
addParameter(parser, 'DurationMin', 60, @(x) isnumeric(x) && isscalar(x) && x > 0);
addParameter(parser, 'Runs', 20, @(x) isnumeric(x) && isscalar(x) && x >= 1);
addParameter(parser, 'ViewDistance', 10, @(x) isnumeric(x) && isscalar(x) && x >= 2);
addParameter(parser, 'XmxSingleGB', 10, @(x) isnumeric(x) && isscalar(x) && x >= 2);
addParameter(parser, 'XmxPackGB', 16, @(x) isnumeric(x) && isscalar(x) && x >= 2);
addParameter(parser, 'Seed', 42, @(x) isnumeric(x) && isscalar(x));
addParameter(parser, 'CpuSingleRel', 0.95, @(x) isnumeric(x) && isscalar(x) && x > 0);
addParameter(parser, 'CpuCores', 1, @(x) isnumeric(x) && isscalar(x) && x >= 1);
addParameter(parser, 'CpuName', 'AMD EPYC (generic)', @(x) ischar(x) || isstring(x));
addParameter(parser, 'SystemRamGB', 32, @(x) isnumeric(x) && isscalar(x) && x >= 4);
addParameter(parser, 'DiskGB', 256, @(x) isnumeric(x) && isscalar(x) && x >= 32);
addParameter(parser, 'DiskType', 'SSD', @(x) ischar(x) || isstring(x));
addParameter(parser, 'ModVersion', '', @(x) ischar(x) || isstring(x));

parse(parser, varargin{:});
cfg = parser.Results;
end

function hw = defaultHardware(cfg)
hw = struct();
hw.name = char(cfg.CpuName);
hw.cpuSingleThreadRel = cfg.CpuSingleRel;
hw.cpuCores = cfg.CpuCores;
hw.systemRamGB = cfg.SystemRamGB;
hw.diskGB = cfg.DiskGB;
hw.diskType = char(cfg.DiskType);
end

function sc = scenarioSingleModHBM(cfg)
sc = struct();
sc.name = 'Single mod: HBM';

sc.heapMaxGB = cfg.XmxSingleGB;
sc.heapStartFrac = 0.42;

% Baseline (ms on i5-9400F main thread)
sc.baseMs = 9.0;
sc.playerMs = 0.28;
sc.chunkMs = 0.00110;
sc.entityMs = 0.00100;
sc.tileMs = 0.0100;
sc.jitterMsStd = 0.65;

% World state sizing
sc.spreadFactor = 0.55;
sc.baseChunks = 700;
sc.entitiesPerPlayerMean = 55;
sc.entitiesPerPlayerStd = 18;
sc.tilesPerPlayerMean = 12;
sc.tilesPerPlayerStd = 8;

% Exploration / worldgen spikes
sc.exploreChancePerPlayerPerTick = 0.00015;
sc.worldgenMu = log(18);   % log-space median
sc.worldgenSigma = 0.60;   % heavier tail => bigger spikes

% Autosave spikes (roughly every ~5 min)
sc.saveChancePerTick = 1 / (20 * 60 * 5);
sc.saveMu = log(30);
sc.saveSigma = 0.50;

% Rare heavy spikes (e.g., explosions / cascaded updates)
sc.rareChancePerTick = 1 / (20 * 60 * 10);
sc.rareMu = log(80);
sc.rareSigma = 0.75;

% Background noise (async tasks stealing time / contention)
sc.bgMu = log(0.9);
sc.bgSigma = 0.35;

% Heap pressure + GC pauses
sc.allocBaseMB = 0.40;
sc.allocPerPlayerMB = 0.035;
sc.allocPerChunkMB = 0.00004;
sc.allocJitterMB = 0.20;
sc.gcBaseMs = 10.0;
sc.gcPerGbMs = 3.2;
sc.gcJitterMs = 5.0;
end

function sc = scenarioModpack(cfg)
sc = struct();
sc.name = 'Modpack (heavier baseline)';

sc.heapMaxGB = cfg.XmxPackGB;
sc.heapStartFrac = 0.50;

% Baseline (ms on i5-9400F main thread)
sc.baseMs = 14.0;
sc.playerMs = 0.36;
sc.chunkMs = 0.00180;
sc.entityMs = 0.00125;
sc.tileMs = 0.0180;
sc.jitterMsStd = 0.95;

% World state sizing (players spread out more, more loaded content)
sc.spreadFactor = 0.65;
sc.baseChunks = 900;
sc.entitiesPerPlayerMean = 75;
sc.entitiesPerPlayerStd = 25;
sc.tilesPerPlayerMean = 26;
sc.tilesPerPlayerStd = 14;

% Exploration / worldgen spikes (heavier terrain/features)
sc.exploreChancePerPlayerPerTick = 0.00022;
sc.worldgenMu = log(25);
sc.worldgenSigma = 0.75;

% Autosave spikes
sc.saveChancePerTick = 1 / (20 * 60 * 5);
sc.saveMu = log(45);
sc.saveSigma = 0.60;

% Rare heavy spikes (pathfinding storms / large factories / chunk loaders)
sc.rareChancePerTick = 1 / (20 * 60 * 5);
sc.rareMu = log(120);
sc.rareSigma = 0.80;

% Background noise (more async workers/mod logic)
sc.bgMu = log(2.2);
sc.bgSigma = 0.45;

% Heap pressure + GC pauses
sc.allocBaseMB = 0.90;
sc.allocPerPlayerMB = 0.060;
sc.allocPerChunkMB = 0.00007;
sc.allocJitterMB = 0.35;
sc.gcBaseMs = 12.0;
sc.gcPerGbMs = 3.6;
sc.gcJitterMs = 6.0;
end

function out = runMonteCarlo(common, sc, hw, runs, seedBase)
metrics = repmat(emptyMetrics(), runs, 1);
traces = cell(runs, 1);

for i = 1:runs
    seed = seedBase + i;
    trace = simulateOnce(common, sc, hw, seed);
    traces{i} = trace;
    metrics(i) = summarizeTrace(trace, sc);
end

avgTps = arrayfun(@(m) m.avgTPS, metrics);
[~, order] = sort(avgTps, 'ascend');
exampleIdx = order(ceil(numel(order) / 2));

out = struct();
out.runs = metrics;
out.summary = summarizeRuns(metrics, sc);
out.example = traces{exampleIdx};
end

function trace = simulateOnce(common, sc, hw, seed)
rng(seed, 'twister');

nTicks = round(common.durationSec * common.tickRate);
tickMs = zeros(nTicks, 1);
tps = zeros(nTicks, 1);
heapUsedGB = zeros(nTicks, 1);
cpuPct = zeros(nTicks, 1);
ramUsedGB = zeros(nTicks, 1);
diskMBps = zeros(nTicks, 1);
netMbps = zeros(nTicks, 1);

gcPauseMs = zeros(nTicks, 1);
worldgenMs = zeros(nTicks, 1);
saveMs = zeros(nTicks, 1);
rareSpikeMs = zeros(nTicks, 1);

players = common.players;
chunksPerPlayer = (2 * common.viewDistance + 1) ^ 2;
updateEveryTicks = common.tickRate; % 1 second
smoothAlpha = 0.15;

heapNow = sc.heapMaxGB * sc.heapStartFrac;

activeChunks = sampleActiveChunks(sc, players, chunksPerPlayer);
entityCount = sampleCount(players, sc.entitiesPerPlayerMean, sc.entitiesPerPlayerStd);
tileCount = sampleCount(players, sc.tilesPerPlayerMean, sc.tilesPerPlayerStd);

for tick = 1:nTicks
    if mod(tick - 1, updateEveryTicks) == 0
        activeChunksNew = sampleActiveChunks(sc, players, chunksPerPlayer);
        entityCountNew = sampleCount(players, sc.entitiesPerPlayerMean, sc.entitiesPerPlayerStd);
        tileCountNew = sampleCount(players, sc.tilesPerPlayerMean, sc.tilesPerPlayerStd);

        activeChunks = round((1 - smoothAlpha) * activeChunks + smoothAlpha * activeChunksNew);
        entityCount = round((1 - smoothAlpha) * entityCount + smoothAlpha * entityCountNew);
        tileCount = round((1 - smoothAlpha) * tileCount + smoothAlpha * tileCountNew);
    end

    ms = sc.baseMs ...
        + sc.playerMs * players ...
        + sc.chunkMs * activeChunks ...
        + sc.entityMs * entityCount ...
        + sc.tileMs * tileCount;

    ms = ms + sc.jitterMsStd * randn;

    % Background jitter (log-normal, always >=0)
    ms = ms + exp(sc.bgMu + sc.bgSigma * randn);

    % New chunk generation spikes
    nWorldgen = sum(rand(players, 1) < sc.exploreChancePerPlayerPerTick);
    if nWorldgen > 0
        wg = sum(exp(sc.worldgenMu + sc.worldgenSigma * randn(nWorldgen, 1)));
        ms = ms + wg;
        worldgenMs(tick) = wg;
    end

    % Autosave spikes
    if rand < sc.saveChancePerTick
        sv = exp(sc.saveMu + sc.saveSigma * randn);
        ms = ms + sv;
        saveMs(tick) = sv;
    end

    % Rare heavy spikes
    if rand < sc.rareChancePerTick
        sp = exp(sc.rareMu + sc.rareSigma * randn);
        ms = ms + sp;
        rareSpikeMs(tick) = sp;
    end

    % Heap growth + GC
    allocMb = sc.allocBaseMB ...
        + sc.allocPerPlayerMB * players ...
        + sc.allocPerChunkMB * activeChunks ...
        + sc.allocJitterMB * randn;
    allocMb = max(0, allocMb);
    heapNow = heapNow + allocMb / 1024;

    if heapNow >= sc.heapMaxGB * common.gcTriggerFrac
        pauseMs = max(0, sc.gcBaseMs + sc.gcPerGbMs * heapNow + sc.gcJitterMs * randn);
        ms = ms + pauseMs;
        gcPauseMs(tick) = pauseMs;
        heapNow = sc.heapMaxGB * common.gcPostFrac + sc.heapMaxGB * (0.05 * rand);
    end

    heapUsedGB(tick) = heapNow;

    ms = max(1.0, ms);

    % Hardware scaling (relative to i5-9400F single-thread)
    ms = ms / hw.cpuSingleThreadRel;

    tickMs(tick) = ms;
    cpuPct(tick) = min(100, 100 * ms / common.tickTargetMs);
    if ms <= common.tickTargetMs
        tps(tick) = common.tickRate;
    else
        tps(tick) = 1000 / ms;
    end

    ramNow = heapNow + common.ramBaseGB + common.ramPerPlayerGB * players + 0.1 * randn;
    ramUsedGB(tick) = min(hw.systemRamGB, max(0, ramNow));

    diskMBps(tick) = common.diskBaseMBps ...
        + common.diskWorldgenMBpsPerMs * worldgenMs(tick) ...
        + common.diskSaveMBpsPerMs * saveMs(tick) ...
        + common.diskRareMBpsPerMs * rareSpikeMs(tick);

    netMbps(tick) = common.netBaseMbps ...
        + common.netPerPlayerMbps * players ...
        + common.netWorldgenMbpsPerMs * worldgenMs(tick) ...
        + common.netSaveMbpsPerMs * saveMs(tick);
end

trace = struct();
trace.scenario = sc.name;
trace.tickMs = tickMs;
trace.tps = tps;
trace.heapUsedGB = heapUsedGB;
trace.cpuPct = cpuPct;
trace.ramUsedGB = ramUsedGB;
trace.diskMBps = diskMBps;
trace.netMbps = netMbps;
trace.gcPauseMs = gcPauseMs;
trace.worldgenMs = worldgenMs;
trace.saveMs = saveMs;
trace.rareSpikeMs = rareSpikeMs;
end

function chunks = sampleActiveChunks(sc, players, chunksPerPlayer)
chunks = sc.baseChunks + players * chunksPerPlayer * sc.spreadFactor;
chunks = chunks * (1 + 0.06 * randn);
chunks = max(0, round(chunks));
end

function count = sampleCount(players, perPlayerMean, perPlayerStd)
perPlayer = perPlayerMean + perPlayerStd * randn(players, 1);
perPlayer = max(0, perPlayer);
count = round(sum(perPlayer));
end

function m = emptyMetrics()
m = struct();
m.avgTPS = NaN;
m.p5TPS = NaN;
m.p1TPS = NaN;
m.minTPS = NaN;
m.avgMSPT = NaN;
m.p95MSPT = NaN;
m.p99MSPT = NaN;
m.maxMSPT = NaN;
m.lagPct = NaN;
m.gcCount = NaN;
m.avgGcPauseMs = NaN;
m.maxGcPauseMs = NaN;
m.heapMaxGB = NaN;
end

function m = summarizeTrace(trace, sc)
tickMs = trace.tickMs;
tps = trace.tps;

m = emptyMetrics();
m.avgTPS = mean(tps);
m.p5TPS = percentile(tps, 5);
m.p1TPS = percentile(tps, 1);
m.minTPS = min(tps);

m.avgMSPT = mean(tickMs);
m.p95MSPT = percentile(tickMs, 95);
m.p99MSPT = percentile(tickMs, 99);
m.maxMSPT = max(tickMs);

m.lagPct = 100 * mean(tickMs > 50);

gc = trace.gcPauseMs;
gcIdx = gc > 0;
m.gcCount = sum(gcIdx);
if any(gcIdx)
    m.avgGcPauseMs = mean(gc(gcIdx));
    m.maxGcPauseMs = max(gc(gcIdx));
else
    m.avgGcPauseMs = 0;
    m.maxGcPauseMs = 0;
end

m.heapMaxGB = sc.heapMaxGB;
end

function s = summarizeRuns(metrics, sc)
avgTPS = arrayfun(@(m) m.avgTPS, metrics);
avgMSPT = arrayfun(@(m) m.avgMSPT, metrics);
lagPct = arrayfun(@(m) m.lagPct, metrics);
p95MSPT = arrayfun(@(m) m.p95MSPT, metrics);
minTPS = arrayfun(@(m) m.minTPS, metrics);
gcCount = arrayfun(@(m) m.gcCount, metrics);

s = struct();
s.name = sc.name;
s.heapMaxGB = sc.heapMaxGB;
s.runs = numel(metrics);

s.avgTPS_mean = mean(avgTPS);
s.avgTPS_p5 = percentile(avgTPS, 5);
s.avgTPS_p95 = percentile(avgTPS, 95);

s.avgMSPT_mean = mean(avgMSPT);
s.p95MSPT_mean = mean(p95MSPT);
s.lagPct_mean = mean(lagPct);

s.minTPS_p5 = percentile(minTPS, 5);
s.gcCount_mean = mean(gcCount);
end

function printComparison(a, b, hw, common)
fprintf('\n=== Forge Server Tick Simulation ===\n');
fprintf('Hardware: %s | cpuSingleRel=%.2f | cpuCores=%d | RAM=%dGB | Disk=%dGB %s\n', ...
    hw.name, hw.cpuSingleThreadRel, hw.cpuCores, hw.systemRamGB, hw.diskGB, hw.diskType);
fprintf('Players=%d | Duration=%.1f min | ViewDistance=%d | Target=%.1f mspt (20 TPS)\n', ...
    common.players, common.durationSec / 60, common.viewDistance, common.tickTargetMs);
if ~isempty(common.modVersion)
    fprintf('Mod build: %s | Threading: single-core tick\n', common.modVersion);
else
    fprintf('Mod build: unknown | Threading: single-core tick\n');
end

fprintf('\n%-26s | avgTPS (p5..p95) | avgMSPT | p95MSPT | lag%% | GC/run\n', 'Scenario');
fprintf('%s\n', repmat('-', 1, 90));

fprintf('%-26s | %5.2f (%5.2f..%5.2f) | %7.2f | %7.2f | %4.1f | %6.2f\n', ...
    a.name, a.avgTPS_mean, a.avgTPS_p5, a.avgTPS_p95, a.avgMSPT_mean, a.p95MSPT_mean, a.lagPct_mean, a.gcCount_mean);
fprintf('%-26s | %5.2f (%5.2f..%5.2f) | %7.2f | %7.2f | %4.1f | %6.2f\n', ...
    b.name, b.avgTPS_mean, b.avgTPS_p5, b.avgTPS_p95, b.avgMSPT_mean, b.p95MSPT_mean, b.lagPct_mean, b.gcCount_mean);

fprintf('\nTip: tune ViewDistance/Xmx and scenario parameters to match your pack (machines, chunkloaders, worldgen).\n\n');
end

function animateResources(singleTrace, packTrace, hw, common)
metricsS = toPerSecondMetrics(singleTrace, common.tickRate);
metricsP = toPerSecondMetrics(packTrace, common.tickRate);
if isempty(metricsS.tMin) || isempty(metricsP.tMin)
    return;
end

n = min(numel(metricsS.tMin), numel(metricsP.tMin));
t = metricsS.tMin(1:n);

cpuS = metricsS.cpuPct(1:n);
cpuP = metricsP.cpuPct(1:n);
ramS = metricsS.ramGB(1:n);
ramP = metricsP.ramGB(1:n);
diskS = metricsS.diskMBps(1:n);
diskP = metricsP.diskMBps(1:n);
netS = metricsS.netMbps(1:n);
netP = metricsP.netMbps(1:n);
tpsS = metricsS.tps(1:n);
tpsP = metricsP.tps(1:n);
msptS = metricsS.mspt(1:n);
msptP = metricsP.mspt(1:n);

maxRam = min(hw.systemRamGB, max([ramS, ramP]) * 1.1);
maxDisk = max(1, max([diskS, diskP]) * 1.1);
maxNet = max(1, max([netS, netP]) * 1.1);
maxMspt = max(60, max([msptS, msptP]) * 1.1);

figure('Name', 'Server Resources (animated)', 'Color', 'w');

subplot(3, 2, 1);
lineCpuS = animatedline('Color', [0 0.45 0.74], 'LineWidth', 1.1); hold on;
lineCpuP = animatedline('Color', [0.85 0.33 0.1], 'LineWidth', 1.1);
grid on;
xlabel('Time (min)');
ylabel('CPU %');
title('CPU (single-core)');
ylim([0 100]);
legend({singleTrace.scenario, packTrace.scenario}, 'Location', 'best');

subplot(3, 2, 2);
lineRamS = animatedline('Color', [0 0.45 0.74], 'LineWidth', 1.1); hold on;
lineRamP = animatedline('Color', [0.85 0.33 0.1], 'LineWidth', 1.1);
grid on;
xlabel('Time (min)');
ylabel('RAM (GB)');
title('Memory usage');
ylim([0 maxRam]);

subplot(3, 2, 3);
lineDiskS = animatedline('Color', [0 0.45 0.74], 'LineWidth', 1.1); hold on;
lineDiskP = animatedline('Color', [0.85 0.33 0.1], 'LineWidth', 1.1);
grid on;
xlabel('Time (min)');
ylabel('Disk MB/s');
title('Disk I/O');
ylim([0 maxDisk]);

subplot(3, 2, 4);
lineNetS = animatedline('Color', [0 0.45 0.74], 'LineWidth', 1.1); hold on;
lineNetP = animatedline('Color', [0.85 0.33 0.1], 'LineWidth', 1.1);
grid on;
xlabel('Time (min)');
ylabel('Net Mbps');
title('Network I/O');
ylim([0 maxNet]);

subplot(3, 2, 5);
lineTpsS = animatedline('Color', [0 0.45 0.74], 'LineWidth', 1.1); hold on;
lineTpsP = animatedline('Color', [0.85 0.33 0.1], 'LineWidth', 1.1);
grid on;
xlabel('Time (min)');
ylabel('TPS');
title('TPS (1s avg)');
ylim([0 20]);

subplot(3, 2, 6);
lineMsptS = animatedline('Color', [0 0.45 0.74], 'LineWidth', 1.1); hold on;
lineMsptP = animatedline('Color', [0.85 0.33 0.1], 'LineWidth', 1.1);
grid on;
xlabel('Time (min)');
ylabel('MSPT');
title('MSPT (1s avg)');
ylim([0 maxMspt]);

overallTitle = sprintf('Resource usage | %s | players=%d | duration=%.1f min', ...
    hw.name, common.players, common.durationSec / 60);
if exist('sgtitle', 'file') == 2
    sgtitle(overallTitle);
else
    annotation('textbox', [0 0.95 1 0.05], 'String', overallTitle, 'EdgeColor', 'none', ...
        'HorizontalAlignment', 'center', 'FontWeight', 'bold');
end

updateEvery = max(1, round(n / 600));
for i = 1:n
    addpoints(lineCpuS, t(i), cpuS(i));
    addpoints(lineCpuP, t(i), cpuP(i));
    addpoints(lineRamS, t(i), ramS(i));
    addpoints(lineRamP, t(i), ramP(i));
    addpoints(lineDiskS, t(i), diskS(i));
    addpoints(lineDiskP, t(i), diskP(i));
    addpoints(lineNetS, t(i), netS(i));
    addpoints(lineNetP, t(i), netP(i));
    addpoints(lineTpsS, t(i), tpsS(i));
    addpoints(lineTpsP, t(i), tpsP(i));
    addpoints(lineMsptS, t(i), msptS(i));
    addpoints(lineMsptP, t(i), msptP(i));
    if mod(i, updateEvery) == 0 || i == n
        drawnow limitrate;
    end
end
end

function plotExample(singleTrace, packTrace, hw, common)
ticksPerSec = common.tickRate;

[tMinS, tpsS, msptS, heapS] = toPerSecond(singleTrace, ticksPerSec);
[tMinP, tpsP, msptP, heapP] = toPerSecond(packTrace, ticksPerSec);

if isempty(tMinS) || isempty(tMinP)
    return;
end

figure('Name', 'Server TPS/MSPT comparison', 'Color', 'w');

subplot(2, 2, 1);
plot(tMinS, tpsS, 'LineWidth', 1.1); hold on;
plot(tMinP, tpsP, 'LineWidth', 1.1);
plot([tMinS(1) tMinS(end)], [20 20], '--', 'HandleVisibility', 'off');
grid on;
xlabel('Time (min)');
ylabel('TPS (1s avg)');
title('TPS over time');
legend({singleTrace.scenario, packTrace.scenario}, 'Location', 'best');

subplot(2, 2, 2);
plot(tMinS, msptS, 'LineWidth', 1.1); hold on;
plot(tMinP, msptP, 'LineWidth', 1.1);
plot([tMinS(1) tMinS(end)], [50 50], '--', 'HandleVisibility', 'off');
grid on;
xlabel('Time (min)');
ylabel('MSPT (1s avg)');
title('Main-thread MSPT over time');

subplot(2, 2, 3);
plot(tMinS, heapS, 'LineWidth', 1.1); hold on;
plot(tMinP, heapP, 'LineWidth', 1.1);
grid on;
xlabel('Time (min)');
ylabel('Heap used (GB)');
title('Heap usage (simplified)');

subplot(2, 2, 4);
plotCdf(singleTrace.tickMs, 'LineWidth', 1.1); hold on;
plotCdf(packTrace.tickMs, 'LineWidth', 1.1);
grid on;
xlabel('MSPT');
ylabel('CDF');
title('MSPT distribution');
legend({singleTrace.scenario, packTrace.scenario}, 'Location', 'best');

overallTitle = sprintf('Minecraft Forge server simulation | %s | players=%d | duration=%.1f min', ...
    hw.name, common.players, common.durationSec / 60);
if exist('sgtitle', 'file') == 2
    sgtitle(overallTitle);
else
    annotation('textbox', [0 0.95 1 0.05], 'String', overallTitle, 'EdgeColor', 'none', ...
        'HorizontalAlignment', 'center', 'FontWeight', 'bold');
end
end

function modVersion = resolveModVersion(modVersion)
modVersion = char(modVersion);
if ~isempty(strtrim(modVersion))
    return;
end

scriptDir = fileparts(mfilename('fullpath'));
searchDir = scriptDir;
for i = 1:5
    candidate = fullfile(searchDir, 'gradle.properties');
    if exist(candidate, 'file') == 2
        modVersion = readGradleModVersion(candidate);
        if ~isempty(modVersion)
            return;
        end
    end
    parentDir = fileparts(searchDir);
    if strcmp(parentDir, searchDir)
        break;
    end
    searchDir = parentDir;
end
modVersion = '';
end

function modVersion = readGradleModVersion(path)
modVersion = '';
fid = fopen(path, 'r');
if fid == -1
    return;
end
cleanupObj = onCleanup(@() fclose(fid));
while true
    line = fgetl(fid);
    if ~ischar(line)
        break;
    end
    line = strtrim(line);
    if isempty(line) || startsWith(line, '#')
        continue;
    end
    if startsWith(line, 'mod_version=')
        modVersion = strtrim(line(numel('mod_version=') + 1:end));
        break;
    end
end
end

function plotMonteCarlo(singleRuns, packRuns)
avgTpsSingle = arrayfun(@(m) m.avgTPS, singleRuns);
avgTpsPack = arrayfun(@(m) m.avgTPS, packRuns);

lagSingle = arrayfun(@(m) m.lagPct, singleRuns);
lagPack = arrayfun(@(m) m.lagPct, packRuns);

figure('Name', 'Monte Carlo summary', 'Color', 'w');

subplot(1, 2, 1);
means = [mean(avgTpsSingle), mean(avgTpsPack)];
low = [percentile(avgTpsSingle, 5), percentile(avgTpsPack, 5)];
high = [percentile(avgTpsSingle, 95), percentile(avgTpsPack, 95)];
bar(means); hold on;
errorbar(1:2, means, means - low, high - means, 'k.', 'LineWidth', 1.1);
set(gca, 'XTickLabel', {'Single mod', 'Modpack'});
grid on;
ylabel('Average TPS per run');
title('Average TPS (mean, p5..p95)');

subplot(1, 2, 2);
meansLag = [mean(lagSingle), mean(lagPack)];
lowLag = [percentile(lagSingle, 5), percentile(lagPack, 5)];
highLag = [percentile(lagSingle, 95), percentile(lagPack, 95)];
bar(meansLag); hold on;
errorbar(1:2, meansLag, meansLag - lowLag, highLag - meansLag, 'k.', 'LineWidth', 1.1);
set(gca, 'XTickLabel', {'Single mod', 'Modpack'});
grid on;
ylabel('Lag ticks (%)');
title('MSPT > 50ms (mean, p5..p95)');
end

function metrics = toPerSecondMetrics(trace, ticksPerSec)
nTicks = numel(trace.tickMs);
nSec = floor(nTicks / ticksPerSec);
if nSec <= 0
    metrics = struct('tMin', [], 'tps', [], 'mspt', [], 'heapGB', [], ...
        'cpuPct', [], 'ramGB', [], 'diskMBps', [], 'netMbps', []);
    return;
end
useN = nSec * ticksPerSec;

metrics = struct();
metrics.tMin = (0:nSec - 1) / 60;
metrics.tps = mean(reshape(trace.tps(1:useN), ticksPerSec, nSec), 1);
metrics.mspt = mean(reshape(trace.tickMs(1:useN), ticksPerSec, nSec), 1);
metrics.heapGB = mean(reshape(trace.heapUsedGB(1:useN), ticksPerSec, nSec), 1);
metrics.cpuPct = mean(reshape(trace.cpuPct(1:useN), ticksPerSec, nSec), 1);
metrics.ramGB = mean(reshape(trace.ramUsedGB(1:useN), ticksPerSec, nSec), 1);
metrics.diskMBps = mean(reshape(trace.diskMBps(1:useN), ticksPerSec, nSec), 1);
metrics.netMbps = mean(reshape(trace.netMbps(1:useN), ticksPerSec, nSec), 1);
end

function [tMin, tps1s, mspt1s, heap1s] = toPerSecond(trace, ticksPerSec)
nTicks = numel(trace.tickMs);
nSec = floor(nTicks / ticksPerSec);
useN = nSec * ticksPerSec;

tpsMat = reshape(trace.tps(1:useN), ticksPerSec, nSec);
msptMat = reshape(trace.tickMs(1:useN), ticksPerSec, nSec);
heapMat = reshape(trace.heapUsedGB(1:useN), ticksPerSec, nSec);

tps1s = mean(tpsMat, 1);
mspt1s = mean(msptMat, 1);
heap1s = mean(heapMat, 1);

tMin = (0:nSec - 1) / 60;
end

function plotCdf(x, varargin)
x = x(:);
x = sort(x(~isnan(x)));
if isempty(x)
    return;
end
y = (1:numel(x)) / numel(x);
plot(x, y, varargin{:});
end

function q = percentile(x, p)
%PERCENTILE Simple percentile without toolboxes (linear interpolation).
x = x(:);
x = x(~isnan(x));
x = sort(x);
if isempty(x)
    q = NaN(size(p));
    return;
end

p = max(0, min(100, p));
q = zeros(size(p));
n = numel(x);

for i = 1:numel(p)
    idx = 1 + (n - 1) * (p(i) / 100);
    lo = floor(idx);
    hi = ceil(idx);
    if lo == hi
        q(i) = x(lo);
    else
        q(i) = x(lo) + (idx - lo) * (x(hi) - x(lo));
    end
end
end
