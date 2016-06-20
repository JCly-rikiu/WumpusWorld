module Map_data

  extend ActiveSupport::Concern

  @@positions = [
    {
      latitude: 25.021651,
      longitude: 121.535189,
      title: '新體',
      connect: [
        { number: 22 },
        { number: 2 },
        { number: 11 }
      ]
    },
    {
      latitude: 25.020344,
      longitude: 121.537628,
      title: '醉月湖',
      connect: [
        { number: 2 },
        { number: 11 },
        { number: 14 }
      ]
    },
    {
      latitude: 25.020708,
      longitude: 121.536405,
      title: '游泳池',
      connect: [
        { number: 0 },
        { number: 1 },
        { number: 5 }
      ]
    },
    {
      latitude: 25.019726,
      longitude: 121.536346,
      title: '舊體',
      connect: [
        { number: 22 },
        { number: 23 },
        { number: 4 },
        { number: 5 }
      ]
    },
    {
      latitude: 25.018894,
      longitude: 121.536302,
      title: '博雅',
      connect: [
        { number: 3 },
        { number: 5 },
        { number: 7 }
      ]
    },
    {
      latitude: 25.018633,
      longitude: 121.537266,
      title: '小福',
      connect: [
        { number: 2 },
        { number: 3 },
        { number: 4 },
        { number: 6 }
      ]
    },
    {
      latitude: 25.017990,
      longitude: 121.540133,
      title: '活大',
      connect: [
        { number: 5 },
        { number: 7 },
        { number: 17 },
        { number: 10 },
        { number: 13 }
      ]
    },
    {
      latitude: 25.017174,
      longitude: 121.536749,
      title: '傅鐘',
      connect: [
        { number: 4 },
        { number: 6 },
        { number: 8 }
      ]
    },
    {
      latitude: 25.016280,
      longitude: 121.536781,
      title: '行政大樓',
      connect: [
        { number: 7 },
        { number: 18 }
      ]
    },
    {
      latitude: 25.017286,
      longitude: 121.540573,
      title: '總圖',
      connect: [
        { number: 10 },
        { number: 13 },
        { number: 19 }
      ]
    },
    {
      latitude: 25.016862,
      longitude: 121.539610,
      title: '保健中心',
      connect: [
        { number: 6 },
        { number: 9 },
        { number: 19 }
      ]
    },
    {
      latitude: 25.020888,
      longitude: 121.539871,
      title: '計中',
      connect: [
        { number: 0 },
        { number: 1 },
        { number: 12 }
      ]
    },
    {
      latitude: 25.020488,
      longitude: 121.540572,
      title: '外教',
      connect: [
        { number: 11 },
        { number: 16 },
        { number: 21 }
      ]
    },
    {
      latitude: 25.017755,
      longitude: 121.541965,
      title: '總圖後草坪',
      connect: [
        { number: 21 },
        { number: 6 },
        { number: 9 }
      ]
    },
    {
      latitude: 25.019731,
      longitude: 121.538439,
      title: '新生',
      connect: [
        { number: 1 },
        { number: 6 },
        { number: 16 }
      ]
    },
    {
      latitude: 25.012936,
      longitude: 121.536696,
      title: '二活',
      connect: [
        { number: 18 },
        { number: 20 },
        { number: 22 }
      ]
    },
    {
      latitude: 25.019428,
      longitude: 121.541518,
      title: '德田館',
      connect: [
        { number: 12 },
        { number: 14 },
        { number: 21 }
      ]
    },
    {
      latitude: 25.016134,
      longitude: 121.537417,
      title: '大福',
      connect: [
        { number: 6 },
        { number: 20 }
      ]
    },
    {
      latitude: 25.015443,
      longitude: 121.537079,
      title: '小小福',
      connect: [
        { number: 8 },
        { number: 23 },
        { number: 15 }
      ]
    },
    {
      latitude: 25.016143,
      longitude: 121.539527,
      title: '生態池',
      connect: [
        { number: 9 },
        { number: 10 },
        { number: 20 }
      ]
    },
    {
      latitude: 25.015173,
      longitude: 121.537634,
      title: '鹿鳴堂',
      connect: [
        { number: 15 },
        { number: 17 },
        { number: 19 }
      ]
    },
    {
      latitude: 25.019855,
      longitude: 121.542338,
      title: '辜振甫圖書館',
      connect: [
        { number: 12 },
        { number: 13 },
        { number: 16 }
      ]
    },
    {
      latitude: 25.017087,
      longitude: 121.534024,
      title: '正門',
      connect: [
        { number: 0 },
        { number: 3 },
        { number: 23 },
        { number: 15 }
      ]
    },
    {
      latitude: 25.017522,
      longitude: 121.535355,
      title: '校史館',
      connect: [
        { number: 22 },
        { number: 3 },
        { number: 18 }
      ]
    }
  ]

  @@egg_positions = [
    {
      latitude: 25.0175631,
      longitude: 121.5335128,
      bearing: 100
    },
    {
      latitude: 25.019726,
      longitude: 121.536346,
      bearing: 172
    },
    {
      latitude: 25.0177,
      longitude: 121.539278,
      bearing: 244
    },
    {
      latitude: 25.0142847,
      longitude: 121.5382578,
      bearing: 316
    },
    {
      latitude: 25.0142002,
      longitude: 121.534694,
      bearing: 28
    }
  ]

  @@easter_egg = {
    view: {
      latitude: 25.016695,
      longitude: 121.5364179,
      bearing: 0
    },
    mac: @@egg_positions,
    line: [
      {
        p1: @@egg_positions[0],
        p2: @@egg_positions[1]
      },
      {
        p1: @@egg_positions[1],
        p2: @@egg_positions[2]
      },
      {
        p1: @@egg_positions[2],
        p2: @@egg_positions[3]
      },
      {
        p1: @@egg_positions[3],
        p2: @@egg_positions[4]
      },
      {
        p1: @@egg_positions[4],
        p2: @@egg_positions[0]
      },
      {
        p1: {
          latitude: 25.0186445,
          longitude: 121.5349294
        },
        p2: {
          latitude: 25.018713,
          longitude: 121.537812
        }
      },
      {
        p1: {
          latitude: 25.018713,
          longitude: 121.537812
        },
        p2: {
          latitude: 25.01599235,
          longitude: 121.5387679
        }
      },
      {
        p1: {
          latitude: 25.01599235,
          longitude: 121.5387679
        },
        p2: {
          latitude: 25.01424245,
          longitude: 121.5364759
        }
      },
      {
        p1: {
          latitude: 25.01424245,
          longitude: 121.5364759
        },
        p2: {
          latitude: 25.01588165,
          longitude: 121.5341034
        }
      },
      {
        p1: {
          latitude: 25.01588165,
          longitude: 121.5341034
        },
        p2: {
          latitude: 25.0186445,
          longitude: 121.5349294
        }
      }
    ]
  }

  def get_positions
    @@positions
  end

  def get_easter_eggs
    @@easter_egg
  end

  def parse_state(state)
    rnt = []

    num = -1
    state.length.times do
      num += 1
      char = state[num]

      checked = (char == '1' ? true : false)
      current = (char == 'C' ? true : false)

      wind = checked || current ? check_wind(num, state) : false
      smell = checked || current ? check_smell(num, state) : false
      go = checked || current ? true : check_go(num, state)
      shoot = checked || current ? false : check_shoot(num, state)

      rnt.push({ is_checked: checked, is_current: current, is_wind: wind, is_smell: smell, can_go: go, can_shoot: shoot })
    end

    rnt
  end

  def check_wind(num, state)
    get_positions[num][:connect].each do |num|
      return true if state[num[:number]] == 'P'
    end

    false
  end

  def check_smell(num, state)
    get_positions[num][:connect].each do |num|
      return true if state[num[:number]] == 'W'
    end

    false
  end

  def check_go(num, state)
    get_positions[num][:connect].each do |num|
      return true if state[num[:number]] == '1'
    end

    false
  end

  def check_shoot(num, state)
    get_positions[num][:connect].each do |num|
      return true if state[num[:number]] == 'C'
    end

    false
  end

  def check_for_win(state)
    num = -1
    state.length.times do
      num += 1
      return false if state[num] == 'W'
    end

    true
  end
end
