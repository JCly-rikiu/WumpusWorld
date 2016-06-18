class StarterController < ApplicationController

  include Map_data

  def init
    render json: { status: 0, data: get_positions }
  end

  def resume
    nickname = params[:nickname]

    if Player.exists?(name: nickname)
      player = Player.find_by_name(nickname)
      render json: { status: 0, data: parse_states(player.state) }
    else
      render json: { status: 1 }
    end
  end

  def start
    nickname = params[:nickname]
    if Player.exists?(name: nickname)
      player = Player.find_by_name(name: nickname)
      player.update(map: create_new_game)
      render json: { status: 0, data: parse_states(player.state) }
    else
      player = Player.create(name: nickname, is_playing: true, state: create_new_game)
      render json: { status: 0, data: parse_states(player.state) }
    end
  end

  private

  def create_new_game
    '000000000000000000000000'
  end

  def parse_states(states)
    rnt = []

    num = -1
    states.length.times do
      num += 1
      char = states[num]

      checked = (char == '1' ? true : false)
      current = (char == 'C' ? true : false)

      wind = check_wind(num, states)
      smell = check_smell(num, states)

      rnt.push({ is_checked: checked, is_current: current, is_wind: wind, is_smell: smell })
    end

    rnt
  end

  def check_wind(num, states)
    get_positions[num][:connect].each do |num|
      return true if states[num[:number]] == 'P'
    end

    false
  end

  def check_smell(num, states)
    get_positions[num][:connect].each do |num|
      return true if states[num[:number]] == 'W'
    end

    false
  end
end
